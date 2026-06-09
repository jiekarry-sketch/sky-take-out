<template>
  <div class="login">
    <div class="login-box">
      <img src="@/assets/login/login-l.png" alt="" />
      <div class="login-form">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
          <div class="login-form-title">
            <span class="title-label">
              <span class="char" style="--i:0">零</span><span class="char" style="--i:1">食</span><span class="char" style="--i:2">星</span><span class="char" style="--i:3">球</span>
            </span>
          </div>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号"
              prefix-icon="iconfont icon-user"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="iconfont icon-lock"
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>
          <el-form-item style="width: 100%">
            <el-button
              :loading="loading"
              class="login-btn"
              size="medium"
              type="primary"
              style="width: 100%"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator'
import { Route } from 'vue-router'
import { Form as ElForm, Input } from 'element-ui'
import { UserModule } from '@/store/modules/user'
import { isValidUsername } from '@/utils/validate'

@Component({
  name: 'Login',
})
export default class extends Vue {
  private validateUsername = (rule: any, value: string, callback: Function) => {
    if (!value) {
      callback(new Error('请输入用户名'))
    } else {
      callback()
    }
  }
  private validatePassword = (rule: any, value: string, callback: Function) => {
    if (value.length < 6) {
      callback(new Error('密码必须在6位以上'))
    } else {
      callback()
    }
  }
  private loginForm = {
    username: 'admin',
    password: '123456',
  } as {
    username: String
    password: String
  }

  loginRules = {
    username: [{ validator: this.validateUsername, trigger: 'blur' }],
    password: [{ validator: this.validatePassword, trigger: 'blur' }],
  }
  private loading = false
  private redirect?: string

  @Watch('$route', { immediate: true })
  private onRouteChange(route: Route) {}

  // 登录
  private handleLogin() {
    ;(this.$refs.loginForm as ElForm).validate(async (valid: boolean) => {
      if (valid) {
        this.loading = true
        await UserModule.Login(this.loginForm as any)
          .then((res: any) => {
            if (String(res.code) === '1') {
              this.$router.push('/')
            } else {
              // this.$message.error(res.msg)
              this.loading = false
            }
          })
          .catch(() => {
            // this.$message.error('用户名或密码错误！')
            this.loading = false
          })
      } else {
        return false
      }
    })
  }
}
</script>

<style lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: linear-gradient(135deg, #120024 0%, #220044 50%, #3B0A68 100%);
  position: relative;
  overflow: hidden;
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background:
      radial-gradient(2px 2px at 20% 30%, rgba(255,255,255,0.3), transparent),
      radial-gradient(2px 2px at 40% 70%, rgba(255,255,255,0.2), transparent),
      radial-gradient(1px 1px at 60% 20%, rgba(255,255,255,0.4), transparent),
      radial-gradient(1px 1px at 80% 50%, rgba(255,255,255,0.3), transparent),
      radial-gradient(2px 2px at 10% 80%, rgba(255,255,255,0.2), transparent),
      radial-gradient(1px 1px at 70% 90%, rgba(255,255,255,0.3), transparent),
      radial-gradient(2px 2px at 90% 10%, rgba(255,255,255,0.2), transparent),
      radial-gradient(1px 1px at 50% 50%, rgba(255,255,255,0.4), transparent);
    animation: twinkle 4s ease-in-out infinite alternate;
  }
}

@keyframes twinkle {
  0% { opacity: 0.7; }
  100% { opacity: 1; }
}

.login-box {
  width: 1000px;
  height: 474.38px;
  border-radius: 24px;
  display: flex;
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
  img {
    width: 60%;
    height: auto;
  }
}

.title {
  margin: 0px auto 10px auto;
  text-align: left;
  color: #707070;
}

.login-form {
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(8px);
  width: 40%;
  border-radius: 0px 24px 24px 0px;
  display: flex;
  justify-content: center;
  align-items: center;
  .el-form {
    width: 214px;
    height: 307px;
  }
  .el-form-item {
    margin-bottom: 30px;
  }
  .el-form-item.is-error .el-input__inner {
    border: 0 !important;
    border-bottom: 1px solid #DC3545 !important;
    background: transparent !important;
  }
  .input-icon {
    height: 32px;
    width: 18px;
    margin-left: -2px;
  }
  .el-input__inner {
    border: 0;
    border-bottom: 1px solid #E0E0E0;
    border-radius: 0;
    font-size: 12px;
    font-weight: 400;
    color: #212121;
    height: 32px;
    line-height: 32px;
    background: transparent;
    &:focus {
      border-bottom: 2px solid #FF6B50;
    }
  }
  .el-input__prefix {
    left: 0;
  }
  .el-input--prefix .el-input__inner {
    padding-left: 26px;
  }
  .el-input__inner::placeholder {
    color: #BDBDBD;
  }
  .el-input__icon {
    color: #757575;
  }
  .el-form-item--medium .el-form-item__content {
    line-height: 32px;
  }
  .el-input--medium .el-input__icon {
    line-height: 32px;
  }
}

.login-btn {
  border-radius: 24px;
  padding: 11px 20px !important;
  margin-top: 10px;
  font-weight: 500;
  font-size: 12px;
  border: 0;
  color: #ffffff;
  background: #FF6B50;
  transition: all 0.2s;
  &:hover,
  &:focus {
    background: #E95A42;
    color: #ffffff;
    transform: translateY(-2px);
  }
}
.login-form-title {
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
  .title-label {
    font-size: 28px;
    font-weight: bold;
    letter-spacing: 6px;
    color: #FFFFFF;
    text-shadow: 0 0 20px rgba(255, 255, 255, 0.4);
    .char {
      display: inline-block;
      animation: bounce-in 0.6s ease calc(var(--i) * 0.1s) both;
    }
  }
}
@keyframes bounce-in {
  0% { opacity: 0; transform: translateY(-20px) scale(0.8); }
  60% { transform: translateY(4px) scale(1.05); }
  100% { opacity: 1; transform: translateY(0) scale(1); }
}
</style>
