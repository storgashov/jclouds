/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrNull;

import javax.inject.Inject;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class ImageForVCloudExpressVAppTemplate implements Function<VCloudExpressVAppTemplate, Image> {
   private final FindLocationForResource findLocationForResource;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider;
   private ReferenceType parent;

   @Inject
   protected ImageForVCloudExpressVAppTemplate(FindLocationForResource findLocationForResource,
         PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider) {
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.credentialsProvider = checkNotNull(credentialsProvider, "credentialsProvider");
   }

   public ImageForVCloudExpressVAppTemplate withParent(ReferenceType parent) {
      this.parent = parent;
      return this;
   }

   @Override
   public Image apply(VCloudExpressVAppTemplate from) {
      ImageBuilder builder = new ImageBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      builder.location(findLocationForResource.apply(checkNotNull(parent, "parent")));
      builder.description(from.getDescription() != null ? from.getDescription() : from.getName());
      builder.operatingSystem(parseOs(from));
      builder.defaultCredentials(credentialsProvider.execute(from));
      return builder.build();
   }

   protected OperatingSystem parseOs(VCloudExpressVAppTemplate from) {
      OperatingSystemBuilder builder = new OperatingSystemBuilder();
      builder.family(parseOsFamilyOrNull("vcloudexpress", checkNotNull(from, "vapp template").getName()));
      builder.description(from.getName());
      builder.is64Bit(from.getName().indexOf("64") != -1);
      return builder.build();
   }

}