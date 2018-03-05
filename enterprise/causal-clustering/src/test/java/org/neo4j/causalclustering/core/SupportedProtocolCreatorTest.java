/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.causalclustering.core;

import co.unruly.matchers.StreamMatchers;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import org.neo4j.causalclustering.protocol.Protocol;
import org.neo4j.causalclustering.protocol.handshake.SupportedProtocols;
import org.neo4j.kernel.configuration.Config;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class SupportedProtocolCreatorTest
{
    @Test
    public void shouldReturnRAftProtocol() throws Throwable
    {
        // given
        Config config = Config.defaults();

        // when
        SupportedProtocols<Protocol.ApplicationProtocol> supportedRaftProtocol = new SupportedProtocolCreator( config ).createSupportedRaftProtocol();

        // then
        assertThat( supportedRaftProtocol.identifier(), equalTo( Protocol.ApplicationProtocolIdentifier.RAFT ) );
    }

    @Test
    public void shouldReturnEmptyVersionSupportedRaftProtocolIfNoVersionsConfigured() throws Throwable
    {
        // given
        Config config = Config.defaults();

        // when
        SupportedProtocols<Protocol.ApplicationProtocol> supportedRaftProtocol = new SupportedProtocolCreator( config ).createSupportedRaftProtocol();

        // then
        assertThat( supportedRaftProtocol.versions(), empty() );
    }

    @Test
    public void shouldReturnConfiguredRaftProtocolVersions() throws Throwable
    {
        // given
        Config config = Config.defaults( CausalClusteringSettings.raft_versions, "2,3,1" );

        // when
        SupportedProtocols<Protocol.ApplicationProtocol> supportedRaftProtocol = new SupportedProtocolCreator( config ).createSupportedRaftProtocol();

        // then
        assertThat( supportedRaftProtocol.versions(), contains( 2,3,1 ) );
    }

    @Test
    public void shouldNotReturnModifiersIfNoVersionsSpecified() throws Throwable
    {
        // given
        Config config = Config.defaults();

        // when
        List<SupportedProtocols<Protocol.ModifierProtocol>> supportedModifierProtocols =
                new SupportedProtocolCreator( config ).createSupportedModifierProtocols();

        // then
        assertThat( supportedModifierProtocols, empty() );
    }

    @Test
    public void shouldReturnCompressionIfVersionsSpecified() throws Throwable
    {
        // given
        Config config = Config.defaults( CausalClusteringSettings.compression_versions, "snappy" );

        // when
        List<SupportedProtocols<Protocol.ModifierProtocol>> supportedModifierProtocols =
                new SupportedProtocolCreator( config ).createSupportedModifierProtocols();

        // then
        Stream<Protocol.Identifier<Protocol.ModifierProtocol>> identifiers = supportedModifierProtocols.stream().map( SupportedProtocols::identifier );
        assertThat( identifiers, StreamMatchers.contains( Protocol.ModifierProtocolIdentifier.COMPRESSION ) );
    }

    @Test
    public void shouldReturnCompressionVersionsSpecified() throws Throwable
    {
        // given
        Config config = Config.defaults( CausalClusteringSettings.compression_versions, "snappy" );

        // when
        List<SupportedProtocols<Protocol.ModifierProtocol>> supportedModifierProtocols =
                new SupportedProtocolCreator( config ).createSupportedModifierProtocols();

        // then
        List<Integer> versions = supportedModifierProtocols.get( 0 ).versions();
        assertThat( versions, contains( Protocol.ModifierProtocols.COMPRESSION_SNAPPY.version() ) );
    }
}
