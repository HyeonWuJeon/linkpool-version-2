package linkpool.query.linkuser.r2dbc

import linkpool.jobgroup.port.`in`.JobGroupResponse
import java.time.LocalDateTime

data class LinkWithUserResult(
    val id: Long,
    val user: UserResult,
    val url: String,
    val title: String? = null,
    val image: String? = null,
    val folderId: Long? = null,
    val describe: String? = null,
    val createdDateTime: LocalDateTime,
)

data class UserResult(
    val id: Long,
    val nickname: String?,
    val jobGroup: JobGroupResponse?,
    val profileImg: String?
)