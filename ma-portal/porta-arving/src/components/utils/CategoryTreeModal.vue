<template>
  <q-input outlined label="Категория" v-model="categoryVariable.name" readonly
           :error="!isValidCategory"
           hide-bottom-space
           error-message="Не заполнено категории операции">
    <template v-slot:append>
      <q-icon
        name="format_list_bulleted"
        :rules="[ val => val.length > 0 || 'Категория операции не указана']">
        <q-popup-proxy cover>
          <div class="q-pa-md q-gutter-sm">
            <q-input ref="filterRef" outlined v-model="filterRow" label="Фильтр">
              <template v-slot:append>
                <q-icon v-if="filterRow!==''" name="clear" class="cursor-pointer" @click="resetFilter"/>
              </template>
            </q-input>

            <q-tree
              :nodes="categoryStore.getTreeCategory"
              node-key="id"
              label-key="name"
              children-key="subCategories"
              v-model:selected="selected"
              :filter="filterRow"
              default-expand-all
              @click="setCategory"
            />
          </div>
        </q-popup-proxy>
      </q-icon>
    </template>
  </q-input>
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
const filterRef = ref(null)
const selected = ref(null)

const categoryStore = useCategoryStore();
if (props.accountId) {
  categoryStore.loadTreeCategory(props.accountId);
  categoryStore.loadCategories(props.accountId);
}

const resetFilter = () => {
  filterRow.value = ''
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
  viewCategoryName();
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
