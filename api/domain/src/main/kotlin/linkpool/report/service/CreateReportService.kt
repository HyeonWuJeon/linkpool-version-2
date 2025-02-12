package linkpool.report.service

import linkpool.common.DomainComponent
import linkpool.exception.DuplicateReportException
import linkpool.report.model.*
import linkpool.report.port.`in`.CreateReportUseCase
import linkpool.report.port.`in`.CreateReportRequest
import linkpool.report.port.out.ReportPort
import javax.transaction.Transactional

@DomainComponent
@Transactional
class CreateReportService(
    private val reportPort: ReportPort,
): CreateReportUseCase {
    override suspend fun create(reporterId: Long, request: CreateReportRequest) {
        val target = ReportTarget(
            targetType = request.targetType,
            targetId = request.targetId
        )
        if (reportPort.findByReporterIdAndTarget(reporterId, target) != null)
            throw DuplicateReportException()
        reportPort.save(
            Report(
                reporterId = reporterId,
                reason = ReportReason(
                    reason = request.reasonType,
                    otherReason = request.otherReason
                ),
                target = target
            )
        )
    }
}