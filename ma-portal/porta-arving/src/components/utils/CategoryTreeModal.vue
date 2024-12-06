<template>
  <q-input outlined label="Категория"
           v-model="categoryVariable.name"
           readonly
           :error="!isValidCategory"
           hide-bottom-space
           @keydown.enter.prevent="showSelectCategoryView"
           error-message="Не заполнено категории операции">
    <template v-slot:append>
      <q-icon
        name="format_list_bulleted"
        :rules="[ (val: string) => val && val.length > 0 || 'Категория операции не указана']"
        @click="showSelectCategoryView">
      </q-icon>
    </template>
  </q-input>

  <q-dialog v-model="showSelectCategory">
    <q-card style="width: 700px; height: 460px">
      <q-card-section>
        <q-input ref="filterRef" outlined v-model="filterRow" label="Фильтр">
          <template v-slot:append>
            <q-icon v-if="filterRow!==''" name="clear" class="cursor-pointer" @click="resetFilter"/>
          </template>
        </q-input>
        <q-checkbox
          v-model="useRelevantCategory"
          label="показывать релевантные категории"
          @click="changeRelevantFlag"/>
      </q-card-section>
      <q-card-section>
        <q-scroll-area style="height: 300px ">
          <q-tree
            :nodes="categoryStore.getTreeCategoryRelevant"
            node-key="id"
            label-key="name"
            children-key="subCategories"
            v-model:selected="selected"
            default-expand-all
            :filter="filterRow"
            @click="setCategory"
          />
        </q-scroll-area>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import {useCategoryStore} from 'stores/categoryStore';
import {Category} from 'src/model/dto/TicketDto';
import {computed, ref} from 'vue';

defineOptions({
  name: 'CategoryTreeModal'
});
const props = defineProps(['accountId']);
const categoryVariable = defineModel<Category>('categoryVariable', {
  default: {
    name: ''
  }
})
const viewName = ref('')
const filterRow = ref('')
const filterRef = ref<null | { focus: () => null }>(null)
const selected = ref(null)
const useRelevantCategory = ref(true)
const showSelectCategory = ref(false)

const categoryStore = useCategoryStore();
if (props.accountId) {
  categoryStore.actionLoadTreeCategoryRelevant(props.accountId, showSelectCategory.value);
  categoryStore.loadCategories(props.accountId);
}

const resetFilter = () => {
  filterRow.value = ''
  if (filterRef.value) {
    filterRef.value.focus;
  }
}

const getCategoryName = (category: Category) => {
  return category.name;
}
const viewCategoryName = () => {
  if (categoryVariable.value) {
    const categoryName = getCategoryName(categoryVariable.value as Category)
    viewName.value = categoryName
    return categoryName;
  }
  return '';
}
viewCategoryName()

const setCategory = () => {
  if (selected.value) {
    const selectedCategory = categoryStore.getCategoryMap.get(selected.value);
    if (selectedCategory) {
      categoryVariable.value = selectedCategory;
      viewName.value = selectedCategory.name;
    }
  }
  showSelectCategory.value = false;
  viewCategoryName();
}

const changeRelevantFlag = () => {
  console.log('event: ' + useRelevantCategory.value)
  categoryStore.actionLoadTreeCategoryRelevant(props.accountId, useRelevantCategory.value);
}

const showSelectCategoryView = () => {
  showSelectCategory.value = true
  if (filterRef.value)
    filterRef.value.focus()
}

const isValidCategory = computed(() => {
  if (categoryVariable.value) {
    return categoryVariable.value.id
  }
  return false;
})
</script>

<style scoped>

</style>
