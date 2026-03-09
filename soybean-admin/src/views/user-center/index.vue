<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { userGenderOptions } from '@/constants/business';
import { fetchGetCurrentUserProfile, fetchUpdateCurrentUserPassword, fetchUpdateCurrentUserProfile } from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { useAuthStore } from '@/store/modules/auth';

const authStore = useAuthStore();
const loading = ref(false);
const submitLoading = ref(false);
const passwordLoading = ref(false);

const { formRules, createConfirmPwdRule, defaultRequiredRule } = useFormRules();

const { formRef: profileFormRef, validate: validateProfile } = useNaiveForm();
const profileModel = reactive<Api.SystemManage.CurrentUserProfile>({
  id: 0,
  userName: '',
  nickName: '',
  userGender: null,
  userPhone: '',
  userEmail: '',
  status: null
});

const profileRules: Record<string, App.Global.FormRule | App.Global.FormRule[]> = {
  nickName: defaultRequiredRule
};

const { formRef: passwordFormRef, validate: validatePassword } = useNaiveForm();
const passwordModel = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const passwordRules: Record<string, App.Global.FormRule | App.Global.FormRule[]> = {
  oldPassword: formRules.pwd,
  newPassword: formRules.pwd,
  confirmPassword: createConfirmPwdRule(computed(() => passwordModel.newPassword))
};

async function getCurrentUserProfile() {
  loading.value = true;
  const { data, error } = await fetchGetCurrentUserProfile();
  if (!error) {
    Object.assign(profileModel, data);
  }
  loading.value = false;
}

async function handleUpdateProfile() {
  await validateProfile();

  submitLoading.value = true;
  const { error } = await fetchUpdateCurrentUserProfile({
    nickName: profileModel.nickName,
    userGender: profileModel.userGender,
    userPhone: profileModel.userPhone,
    userEmail: profileModel.userEmail
  });

  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    await getCurrentUserProfile();
  }
  submitLoading.value = false;
}

async function handleUpdatePassword() {
  await validatePassword();

  passwordLoading.value = true;
  const { error } = await fetchUpdateCurrentUserPassword({
    oldPassword: passwordModel.oldPassword,
    newPassword: passwordModel.newPassword
  });

  if (!error) {
    window.$message?.success($t('page.userCenter.password.updatedRelogin'));
    await authStore.resetStore();
  }
  passwordLoading.value = false;
}

onMounted(() => {
  getCurrentUserProfile();
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NCard :title="$t('common.userCenter')" :bordered="false" size="small" class="card-wrapper">
      <NSpin :show="loading">
        <NGrid cols="1 l:2" :x-gap="24" :y-gap="24" responsive="screen">
          <NGi>
            <NCard :title="$t('page.userCenter.profile.title')" size="small">
              <NForm ref="profileFormRef" :model="profileModel" :rules="profileRules" label-placement="left" label-width="90">
                <NFormItem :label="$t('page.userCenter.profile.userName')">
                  <NInput :value="profileModel.userName" disabled />
                </NFormItem>
                <NFormItem :label="$t('page.userCenter.profile.nickName')" path="nickName">
                  <NInput v-model:value="profileModel.nickName" :placeholder="$t('page.userCenter.profile.nickNamePlaceholder')" />
                </NFormItem>
                <NFormItem :label="$t('page.userCenter.profile.gender')">
                  <NRadioGroup v-model:value="profileModel.userGender">
                    <NRadio v-for="item in userGenderOptions" :key="item.value" :value="item.value" :label="$t(item.label)" />
                  </NRadioGroup>
                </NFormItem>
                <NFormItem :label="$t('page.userCenter.profile.phone')" path="userPhone">
                  <NInput v-model:value="profileModel.userPhone" :placeholder="$t('page.userCenter.profile.phonePlaceholder')" />
                </NFormItem>
                <NFormItem :label="$t('page.userCenter.profile.email')" path="userEmail">
                  <NInput v-model:value="profileModel.userEmail" :placeholder="$t('page.userCenter.profile.emailPlaceholder')" />
                </NFormItem>
                <NFormItem>
                  <NButton type="primary" :loading="submitLoading" @click="handleUpdateProfile">
                    {{ $t('page.userCenter.profile.save') }}
                  </NButton>
                </NFormItem>
              </NForm>
            </NCard>
          </NGi>

          <NGi>
            <NCard :title="$t('page.userCenter.password.title')" size="small">
              <NForm ref="passwordFormRef" :model="passwordModel" :rules="passwordRules" label-placement="left" label-width="90">
                <NFormItem :label="$t('page.userCenter.password.oldPassword')" path="oldPassword">
                  <NInput
                    v-model:value="passwordModel.oldPassword"
                    type="password"
                    show-password-on="click"
                    :placeholder="$t('page.userCenter.password.oldPasswordPlaceholder')"
                  />
                </NFormItem>
                <NFormItem :label="$t('page.userCenter.password.newPassword')" path="newPassword">
                  <NInput
                    v-model:value="passwordModel.newPassword"
                    type="password"
                    show-password-on="click"
                    :placeholder="$t('page.userCenter.password.newPasswordPlaceholder')"
                  />
                </NFormItem>
                <NFormItem :label="$t('page.userCenter.password.confirmPassword')" path="confirmPassword">
                  <NInput
                    v-model:value="passwordModel.confirmPassword"
                    type="password"
                    show-password-on="click"
                    :placeholder="$t('page.userCenter.password.confirmPasswordPlaceholder')"
                  />
                </NFormItem>
                <NFormItem>
                  <NButton type="primary" :loading="passwordLoading" @click="handleUpdatePassword">
                    {{ $t('page.userCenter.password.save') }}
                  </NButton>
                </NFormItem>
              </NForm>
            </NCard>
          </NGi>
        </NGrid>
      </NSpin>
    </NCard>
  </div>
</template>

<style scoped></style>
