// Copyright 2026 The ThunderID Authors
// SPDX-License-Identifier: Apache-2.0

package dev.thunderid.android.management

import dev.thunderid.android.ThunderIDFetcher

/**
 * Flow definition management operations, reached through `ThunderIDClient.flows`, against the `flows`
 * collection. These manage the flow graphs; running a flow is the sign-in, sign-up and administration
 * operations' job. Each call accepts an optional [ThunderIDFetcher] that takes precedence over
 * `ThunderIDConfig.http.fetcher`.
 */
class FlowsClient internal constructor(
    private val transport: ManagementTransport,
) {
    /**
     * Returns a page of flow definitions.
     *
     * @param flowType Only flows of this type, such as `AUTHENTICATION` or `REGISTRATION`.
     */
    suspend fun list(
        flowType: String? = null,
        limit: Int? = null,
        offset: Int? = null,
        fetcher: ThunderIDFetcher? = null,
    ): FlowListResponse = transport.list(mapOf("flowType" to flowType) + page(limit, offset), fetcher)

    /** Returns a single flow definition with its graph. */
    suspend fun get(
        id: String,
        fetcher: ThunderIDFetcher? = null,
    ): FlowDefinition = transport.get(id, fetcher = fetcher)

    /** Creates a flow definition and returns it as the server stored it. */
    suspend fun create(
        flow: FlowDefinitionRequest,
        fetcher: ThunderIDFetcher? = null,
    ): FlowDefinition = transport.create(flow, fetcher)

    /** Replaces a flow definition, which creates a new version, and returns the updated flow. */
    suspend fun update(
        id: String,
        flow: FlowDefinitionRequest,
        fetcher: ThunderIDFetcher? = null,
    ): FlowDefinition = transport.update(id, flow, fetcher)

    /** Deletes a flow definition. A declarative (read-only) flow cannot be deleted. */
    suspend fun delete(
        id: String,
        fetcher: ThunderIDFetcher? = null,
    ) = transport.delete(id, fetcher)

    /** Returns the version history of a flow, most recent first. */
    suspend fun listVersions(
        id: String,
        fetcher: ThunderIDFetcher? = null,
    ): FlowVersionListResponse = transport.request("GET", listOf(id, "versions"), fetcher = fetcher)

    /** Returns a flow definition as it was at [version]. */
    suspend fun getVersion(
        id: String,
        version: Int,
        fetcher: ThunderIDFetcher? = null,
    ): FlowVersion = transport.request("GET", listOf(id, "versions", version.toString()), fetcher = fetcher)

    /**
     * Restores a flow to [version], which creates a new version with that version's content, and returns
     * the updated flow.
     */
    suspend fun restoreVersion(
        id: String,
        version: Int,
        fetcher: ThunderIDFetcher? = null,
    ): FlowDefinition {
        val body = mapOf("version" to version)
        return transport.request("POST", listOf(id, "restore"), body = body, fetcher = fetcher)
    }

    /**
     * Returns the resources that reference a flow, such as applications that sign in with it. Informational:
     * the server does not refuse to delete a flow that is in use.
     */
    suspend fun getUsages(
        id: String,
        fetcher: ThunderIDFetcher? = null,
    ): ResourceUsagesResponse = transport.getSubResource(id, "usages", emptyMap(), fetcher)
}
