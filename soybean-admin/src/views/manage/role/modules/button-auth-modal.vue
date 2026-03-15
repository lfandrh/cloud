<script setup lang="ts">
import { computed, ref, shallowRef, watch } from 'vue';
import { fetchGetButtonTree, fetchGetRoleButtonIds, fetchUpdateRoleButtons } from '@/service/api';
import { $t } from '@/locales';

defineOptions({
  name: 'ButtonAuthModal'
});

interface Props {
  /** the roleId */
  roleId: number;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', {
  default: false
});

function closeModal() {
  visible.value = false;
}

const title = computed(() => $t('common.edit') + $t('page.manage.role.buttonAuth'));

type ButtonConfig = {
  key: string;
  label: string;
  disabled?: boolean;
  children?: ButtonConfig[];
};

const tree = shallowRef<ButtonConfig[]>([]);
const expandedKeys = ref<string[]>([]);
const checks = ref<string[]>([]);

const hasButtonNodes = computed(() => tree.value.some(item => item.children?.length));

async function getAllButtons() {
  const { error, data } = await fetchGetButtonTree();

  if (!error) {
    tree.value = (data || []) as ButtonConfig[];
    expandedKeys.value = tree.value.map(item => item.key);
  }
}

async function getChecks() {
  const { error, data } = await fetchGetRoleButtonIds(props.roleId);

  if (!error) {
    checks.value = (data || []).map(id => `btn-${id}`);
  }
}

function handleCheckedKeysUpdate(keys: Array<string | number>) {
  checks.value = keys.map(key => String(key));
}

async function handleSubmit() {
  const buttonIds = checks.value
    .filter(key => key.startsWith('btn-'))
    .map(key => Number(key.slice(4)))
    .filter(id => Number.isFinite(id) && id > 0);

  const { error } = await fetchUpdateRoleButtons(props.roleId, buttonIds);

  if (!error) {
    window.$message?.success?.($t('common.modifySuccess'));
    closeModal();
  }
}

async function init() {
  await getAllButtons();
  await getChecks();
}

watch(visible, val => {
  if (val) {
    void init();
  }
});
</script>

<template>
  <NModal v-model:show="visible" :title="title" preset="card" class="w-480px">
    <NTree
      v-if="hasButtonNodes"
      v-model:checked-keys="checks"
      v-model:expanded-keys="expandedKeys"
      :data="tree"
      key-field="key"
      checkable
      expand-on-click
      virtual-scroll
      block-line
      default-expand-all
      class="h-280px"
      :checked-keys="checks"
      @update:checked-keys="handleCheckedKeysUpdate"
    />
    <NEmpty v-else description="当前没有可分配的按钮权限" class="h-280px justify-center" />
    <template #footer>
      <NSpace justify="end">
        <NButton size="small" class="mt-16px" @click="closeModal">
          {{ $t('common.cancel') }}
        </NButton>
        <NButton type="primary" size="small" class="mt-16px" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </NButton>
      </NSpace>
    </template>
  </NModal>
</template>

<style scoped></style>
