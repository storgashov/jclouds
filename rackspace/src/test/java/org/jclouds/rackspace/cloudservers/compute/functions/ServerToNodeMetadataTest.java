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

package org.jclouds.rackspace.cloudservers.compute.functions;

import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.rackspace.cloudservers.compute.config.CloudServersComputeServiceContextModule;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.rackspace.cloudservers.functions.ParseServerFromJsonResponseTest;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ServerToNodeMetadataTest")
public class ServerToNodeMetadataTest {
   Location provider = new LocationImpl(LocationScope.ZONE, "dallas", "description", null);

   @Test
   public void testApplyWhereImageAndHardwareNotFoundButCredentialsFound()
         throws UnknownHostException {
      Credentials creds = new Credentials("root", "abdce");

      Map<ServerStatus, NodeState> serverStateToNodeState = CloudServersComputeServiceContextModule.serverToNodeState;
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of();
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of();
      Server server = ParseServerFromJsonResponseTest.parseServer();

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState,
            ImmutableMap.<String, Credentials> of("1234", creds), Suppliers.<Set<? extends Image>> ofInstance(images),
            Suppliers.ofInstance(provider), Suppliers.<Set<? extends Hardware>> ofInstance(hardwares));

      NodeMetadata metadata = parser.apply(server);

      assertEquals(
            metadata,
            new NodeMetadataBuilder()
                  .state(NodeState.PENDING)
                  .publicAddresses(ImmutableSet.of("67.23.10.132", "67.23.10.131"))
                  .privateAddresses(ImmutableSet.of("10.176.42.16"))
                  .tag("NOTAG-sample-server")
                  .imageId("2")
                  .id("1234")
                  .providerId("1234")
                  .name("sample-server")
                  .credentials(creds)
                  .location(
                        new LocationImpl(LocationScope.HOST, "e4d909c290d0fb1ca068ffaddf22cbd0",
                              "e4d909c290d0fb1ca068ffaddf22cbd0", new LocationImpl(LocationScope.ZONE, "dallas",
                                    "description", null)))
                  .userMetadata(ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1")).build());
   }

   @Test
   public void testApplyWhereImageAndHardwareNotFound() throws UnknownHostException {
      Map<ServerStatus, NodeState> serverStateToNodeState = CloudServersComputeServiceContextModule.serverToNodeState;
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of();
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of();
      Server server = ParseServerFromJsonResponseTest.parseServer();

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState,
            ImmutableMap.<String, Credentials> of(), Suppliers.<Set<? extends Image>> ofInstance(images),
            Suppliers.ofInstance(provider), Suppliers.<Set<? extends Hardware>> ofInstance(hardwares));

      NodeMetadata metadata = parser.apply(server);

      assertEquals(
            metadata,
            new NodeMetadataBuilder()
                  .state(NodeState.PENDING)
                  .publicAddresses(ImmutableSet.of("67.23.10.132", "67.23.10.131"))
                  .privateAddresses(ImmutableSet.of("10.176.42.16"))
                  .tag("NOTAG-sample-server")
                  .imageId("2")
                  .id("1234")
                  .providerId("1234")
                  .name("sample-server")
                  .location(
                        new LocationImpl(LocationScope.HOST, "e4d909c290d0fb1ca068ffaddf22cbd0",
                              "e4d909c290d0fb1ca068ffaddf22cbd0", new LocationImpl(LocationScope.ZONE, "dallas",
                                    "description", null)))
                  .userMetadata(ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1")).build());

   }

   @Test
   public void testApplyWhereImageFoundAndHardwareNotFound() throws UnknownHostException {
      Map<ServerStatus, NodeState> serverStateToNodeState = CloudServersComputeServiceContextModule.serverToNodeState;
      org.jclouds.compute.domain.Image jcImage = CloudServersImageToImageTest.convertImage();
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of(jcImage);
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of();
      Server server = ParseServerFromJsonResponseTest.parseServer();

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState,
            ImmutableMap.<String, Credentials> of(), Suppliers.<Set<? extends Image>> ofInstance(images),
            Suppliers.ofInstance(provider), Suppliers.<Set<? extends Hardware>> ofInstance(hardwares));

      NodeMetadata metadata = parser.apply(server);

      assertEquals(
            metadata,
            new NodeMetadataBuilder()
                  .state(NodeState.PENDING)
                  .publicAddresses(ImmutableSet.of("67.23.10.132", "67.23.10.131"))
                  .privateAddresses(ImmutableSet.of("10.176.42.16"))
                  .tag("NOTAG-sample-server")
                  .imageId("2")
                  .operatingSystem(
                        new OperatingSystemBuilder().family(OsFamily.CENTOS).description("CentOS 5.2").is64Bit(true)
                              .build())
                  .id("1234")
                  .providerId("1234")
                  .name("sample-server")
                  .location(
                        new LocationImpl(LocationScope.HOST, "e4d909c290d0fb1ca068ffaddf22cbd0",
                              "e4d909c290d0fb1ca068ffaddf22cbd0", new LocationImpl(LocationScope.ZONE, "dallas",
                                    "description", null)))
                  .userMetadata(ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1")).build());

   }

   @Test
   public void testApplyWhereImageAndHardwareFound() throws UnknownHostException {
      Map<ServerStatus, NodeState> serverStateToNodeState = CloudServersComputeServiceContextModule.serverToNodeState;
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of(CloudServersImageToImageTest.convertImage());
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of(FlavorToHardwareTest.convertFlavor());
      Server server = ParseServerFromJsonResponseTest.parseServer();

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState,
            ImmutableMap.<String, Credentials> of(), Suppliers.<Set<? extends Image>> ofInstance(images),
            Suppliers.ofInstance(provider), Suppliers.<Set<? extends Hardware>> ofInstance(hardwares));

      NodeMetadata metadata = parser.apply(server);

      assertEquals(
            metadata,
            new NodeMetadataBuilder()
                  .state(NodeState.PENDING)
                  .publicAddresses(ImmutableSet.of("67.23.10.132", "67.23.10.131"))
                  .privateAddresses(ImmutableSet.of("10.176.42.16"))
                  .tag("NOTAG-sample-server")
                  .imageId("2")
                  .hardware(
                        new HardwareBuilder()
                              .ids("1")
                              .name("256 MB Server")
                              .processors(ImmutableList.of(new Processor(1.0, 1.0)))
                              .ram(256)
                              .volumes(
                                    ImmutableList.of(new VolumeBuilder().type(Volume.Type.LOCAL).size(10.0f)
                                          .durable(true).bootDevice(true).build())).build())
                  .operatingSystem(
                        new OperatingSystemBuilder().family(OsFamily.CENTOS).description("CentOS 5.2").is64Bit(true)
                              .build())
                  .id("1234")
                  .providerId("1234")
                  .name("sample-server")
                  .location(
                        new LocationImpl(LocationScope.HOST, "e4d909c290d0fb1ca068ffaddf22cbd0",
                              "e4d909c290d0fb1ca068ffaddf22cbd0", new LocationImpl(LocationScope.ZONE, "dallas",
                                    "description", null)))
                  .userMetadata(ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1")).build());
   }
}