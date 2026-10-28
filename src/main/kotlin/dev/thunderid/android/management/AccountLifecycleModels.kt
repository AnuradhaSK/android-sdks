// Copyright 2026 The ThunderID Authors
// SPDX-License-Identifier: Apache-2.0

package dev.thunderid.android.management

import dev.thunderid.android.IAMException
import dev.thunderid.android.ThunderIDErrorCode
import dev.thunderid.android.ThunderIDFetcher

/**
 * The effective access status of a user, agent or application, computed by the server when the record is
 * read or updated and never sent on a write.
 *
 * @property value `ACTIVE`, `LOCKED` or `SUSPENDED` (applications are never `LOCKED`). See [AccessStatusValue].
 * @property details Why the status applies and how it is cleared. Absent for `ACTIVE` and for applications.
 */
data class AccessStatus(
    val value: String? = null,
    val details: AccessStatusDetails? = null,
)

/** The values [AccessStatus.value] takes. */
object AccessStatusValue {
    const val ACTIVE = "ACTIVE"
    const val LOCKED = "LOCKED"
    const val SUSPENDED = "SUSPENDED"
}

/**
 * Why an account is locked or suspended.
 *
 * @property reason `FAILED_ATTEMPTS`, `SUSPENSION_RELEASE_LOCK` or `ADMINISTRATIVE_SUSPENSION`.
 * @property since When the operator placed the hold. Absent for an automatic lock.
 * @property operatorNote The note the operator entered when placing the hold.
 * @property lockedMethods The automatic locks in force. Present only while the status is `LOCKED`.
 */
data class AccessStatusDetails(
    val reason: String? = null,
    val since: String? = null,
    val operatorNote: String? = null,
    val lockedMethods: List<LockedMethod>? = null,
)

/** One automatic lock: the locked [method] (or `entity` for the whole account) and when it expires, if it does. */
data class LockedMethod(
    val method: String? = null,
    val expiresAt: String? = null,
)

/**
 * The result of an account lifecycle operation such as `UsersClient.suspend`.
 *
 * @property emailSent Whether the flow's notification email was sent: `false` when a best-effort email step
 *   sent nothing, and `null` when the flow has no email step.
 */
data class AccountLifecycleResult(
    val emailSent: Boolean? = null,
)

/**
 * Runs a flow-only account lifecycle operation, which has no API to fall back to: with no flow configured
 * for [configKey] it throws [AdministrationFlowError] with the code `{operation}-NotConfiguredError-001`
 * rather than doing nothing.
 */
internal suspend fun AdministrationClient.runLifecycleFlow(
    configKey: String,
    operation: String,
    action: String,
    subject: String,
    operatorNote: String?,
    fetcher: ThunderIDFetcher?,
): AccountLifecycleResult {
    if (subject.isBlank()) {
        throw IAMException(
            ThunderIDErrorCode.INVALID_INPUT,
            "A resource identifier is required",
        )
    }
    val inputs =
        buildMap {
            put(AdministrationFlowInput.SUBJECT, subject)
            operatorNote?.takeIf { it.isNotEmpty() }?.let { put(AdministrationFlowInput.OPERATOR_NOTE, it) }
        }
    val data =
        runFlow(configKey, inputs, action, fetcher)
            ?: throw AdministrationFlowError(
                "$operation-NotConfiguredError-001",
                "No $action flow is configured on the server. The operation runs only through its " +
                    "administration flow, so it was not performed.",
            )
    val emailSent =
        when (data[AdministrationFlowData.EMAIL_SENT]) {
            "true" -> true
            "false" -> false
            else -> null
        }
    return AccountLifecycleResult(emailSent)
}
