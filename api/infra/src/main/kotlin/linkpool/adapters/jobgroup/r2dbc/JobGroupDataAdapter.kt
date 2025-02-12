package linkpool.adapters.jobgroup.r2dbc

import kotlinx.coroutines.reactive.awaitSingle
import linkpool.adapters.jobgroup.r2dbc.entity.JobGroupR2dbcEntity
import linkpool.adapters.jobgroup.r2dbc.repository.JobGroupRepository
import linkpool.adapters.jobgroup.r2dbc.repository.getById
import linkpool.jobgroup.model.JobGroup
import linkpool.jobgroup.port.out.JobGroupPort
import org.springframework.stereotype.Service


@Service
class JobGroupDataAdapter(
    private val jobGroupRepository: JobGroupRepository
) : JobGroupPort {
    override suspend fun findAll() = toModel(jobGroupRepository.findAll()
        .collectList()
        .awaitSingle()
    )

    override suspend fun findById(id: Long) = toModel(jobGroupRepository.getById(id).awaitSingle())

    private fun toModel(entities: List<JobGroupR2dbcEntity>) =
        entities.map { toModel(it) }

    private fun toModel(entity: JobGroupR2dbcEntity) =
        JobGroup(id = entity.id, name = entity.name)

}