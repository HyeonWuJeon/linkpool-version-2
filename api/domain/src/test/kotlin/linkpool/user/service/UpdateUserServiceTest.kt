package linkpool.user.service

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import linkpool.exception.DuplicateNicknameException
import linkpool.support.spec.afterRootTest
import linkpool.user.fixtures.createUser
import linkpool.user.fixtures.createUserInfoRequest
import linkpool.user.port.out.UserPort

class UpdateUserServiceTest: BehaviorSpec({
    val userPort = mockk<UserPort>()
    val updateUserService = UpdateUserService(userPort)

    Given("수정하려하는 닉네임이 이미 존재하는 닉네임인 경우") {
        val user = createUser()
        val userInfoRequest = createUserInfoRequest()
        every { userPort.existsByInfoNickname(userInfoRequest.nickname!!) } returns true

        When("닉네임을 포함하여 프로필 수정을 하면") {
            Then("DuplicateNicknameException 예외가 발생한다") {
                shouldThrow<DuplicateNicknameException> {
                    updateUserService.updateUserInfo(user, userInfoRequest)
                }
            }
        }
    }

    Given("수정하려하는 닉네임이 중복되지 않는 닉네임인 경우") {
        val user = createUser()
        val userInfoRequest = createUserInfoRequest()
        every { userPort.existsByInfoNickname(userInfoRequest.nickname!!) } returns false
        every { userPort.patch(any()) } just Runs

        When("닉네임을 포함하여 프로필 수정을 하면") {
            Then("DuplicateNicknameException 예외가 발생하지 않고, 프로필이 수정된다.") {
                shouldNotThrow<DuplicateNicknameException> {
                    updateUserService.updateUserInfo(user, userInfoRequest)
                }
                user.info!!.nickname shouldBe userInfoRequest.nickname
                user.info!!.jobGroupId shouldBe userInfoRequest.jobGroupId
                user.info!!.profileImage shouldBe userInfoRequest.profileImage
                verify { userPort.patch(user) }
            }
        }
    }

    Given("닉네임 정보를 제외한 다른 정보만 수정하는 경우") {
        val user = createUser()
        val userInfoRequest = createUserInfoRequest(nickname = null)
        every { userPort.patch(any()) } just Runs

        When("프로필 수정을 하면") {
            val originalNickName = user.info!!.nickname
            updateUserService.updateUserInfo(user, userInfoRequest)
            Then("요청한 정보만 수정된다.") {
                user.info!!.nickname shouldBe originalNickName
                user.info!!.jobGroupId shouldBe userInfoRequest.jobGroupId
                user.info!!.profileImage shouldBe userInfoRequest.profileImage
                verify { userPort.patch(user) }
            }
        }
    }

    afterRootTest {
        clearAllMocks()
    }

})