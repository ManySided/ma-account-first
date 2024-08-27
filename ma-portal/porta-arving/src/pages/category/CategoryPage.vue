<template>
  <q-page>
    <div class="q-pa-md example-row-equal-width">
      <div class="row justify-center">
        <div class="col-md-4">
          <q-card flat bordered>
            <q-card-section class="q-pt-xs">
              <div class="text-overline">Список категорий</div>
              <div class="q-mt-sm q-mb-xs">
                <q-input outlined v-model="filterRow" label="Фильтр" style="padding: 3px">
                  <template v-slot:append>
                    <q-icon name="close" @click="clearFilterMethod"/>
                  </template>
                </q-input>
              </div>
            </q-card-section>
            <q-card-section>
              <q-scroll-area :style="{ height: windowHeight-280+'px', width: 'auto' }">
                <q-tree
                  :nodes="storeCategory.getTreeCategory"
                  node-key="id"
                  label-key="name"
                  children-key="subCategories"
                  v-model:selected="selected"
                  selected-color="primary"
                  :filter="filterRow"
                  @click="setCategoryMethod"
                />
              </q-scroll-area>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-md-4" style="padding-left: 3px">
          <q-card flat bordered>
            <q-card-section class="q-pt-xs">
              <div class="text-overline">Релактирование категорий</div>
            </q-card-section>
            <q-card-section>
              <q-input outlined v-model="currentCategory.name" label="Название" style="padding-bottom: 3px"/>
              <q-select
                outlined
                v-model="currentCategory.parent"
                :options="storeCategory.getTreeCategory"
                option-value="id"
                option-label="name"
                emit-value
                map-options
                label="Родительская категория">
                <template v-slot:append>
                  <q-icon name="close" @click="clearParentCategoryMethod"/>
                </template>
              </q-select>
            </q-card-section>
            <q-separator dark/>

            <q-card-actions align="right">
              <q-btn flat @click="saveCategoryMethod">{{ currentCategory.id ? "Сохранить" : "Создать" }}</q-btn>
              <q-btn flat v-if="currentCategory.id" @click="openDeleteCategoryViewMethod">Удалить</q-btn>
              <q-btn flat @click="clearCategoryMethod">Очистить</q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </div>

    <q-dialog v-model="deleteCategoryView" persistent backdrop-filter="blur(4px)">
      <q-card style="width: 600px">
        <q-bar>
          <div>Удаление категории</div>
          <q-space/>
          <q-btn dense flat icon="close" v-close-popup>
            <q-tooltip class="bg-white text-primary">Close</q-tooltip>
          </q-btn>
        </q-bar>
        <q-card-section>
          Удалить операция "{{ currentCategory.name }}"?
        </q-card-section>
        <q-card-section>
          <q-select
            outlined
            v-model="reserveCategory"
            :options="storeCategory.getArrayCategoryWithoutCurrent"
            option-value="id"
            option-label="name"
            emit-value
            map-options
            label="Резервная категория">
            <template v-slot:append>
              <q-icon name="close" @click="clearReserveCategoryMethod"/>
            </template>
          </q-select>
          <q-banner inline-actions>
            <p style="text-align: justify; font-style: italic;">Если на категории есть операции, необходимо указать
              категорию, на которую будут переведуные все
              операции</p>
          </q-banner>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Отмена" v-close-popup/>
          <q-btn flat label="Удалить" v-close-popup @click="deleteCategoryMethod"/>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useStuffStore} from 'stores/stuffStore';
import {useCategoryStore} from 'stores/categoryStore';
import {CategoryDeleteRequest, getClearCategory} from 'src/model/dto/TicketDto';

export default defineComponent({
  name: 'CategoryPage',

  setup() {
    // store
    const storeStuff = useStuffStore();
    const storeCategory = useCategoryStore();

    // init
    const windowWidth = ref(window.innerWidth)
    const windowHeight = ref(window.innerHeight)

    function updateCategoryListMethod() {
      if (storeStuff.getAccountId) {
        storeCategory.loadTreeCategory(storeStuff.getAccountId);
        storeCategory.loadCategories(storeStuff.getAccountId);
      }
    }

    storeStuff.actionUpdateTitlePage('Категории');
    updateCategoryListMethod()

    if (storeStuff.getAccountId) {
      storeCategory.loadTreeCategory(storeStuff.getAccountId);
      storeCategory.loadCategories(storeStuff.getAccountId);
    }

    // variable
    const selected = ref(null)
    const filterRow = ref('')
    const currentCategory = ref(getClearCategory())
    const reserveCategory = ref<null | number>(null)
    const deleteCategoryView = ref(false)

    // methods
    const setCategoryMethod = () => {
      if (selected.value) {
        const selectedCategory = storeCategory.getCategoryMap.get(selected.value);
        if (selectedCategory) {
          currentCategory.value = selectedCategory;
        }
      }
    }

    const clearFilterMethod = () => {
      filterRow.value = '';
    }
    const clearParentCategoryMethod = () => {
      currentCategory.value.parent = undefined;
    }
    const clearReserveCategoryMethod = () => {
      reserveCategory.value = null
    }
    const clearCategoryMethod = () => {
      currentCategory.value = getClearCategory()
    }
    const saveCategoryMethod = () => {
      if (currentCategory.value && !currentCategory.value.accountId)
        currentCategory.value.accountId = storeStuff.getAccountId
      storeCategory.actionSaveCategory(
        currentCategory.value,
        () => {
          updateCategoryListMethod();
          clearCategoryMethod();
        }
      )
    }
    const openDeleteCategoryViewMethod = () => {
      deleteCategoryView.value = true
      storeCategory.actionSetCurrentCategory(currentCategory.value)
    }
    const deleteCategoryMethod = () => {
      storeCategory.actionDeleteCategory(
        {
          categoryId: currentCategory.value.id,
          reserveCategoryId: reserveCategory.value
        } as CategoryDeleteRequest,
        () => {
          updateCategoryListMethod();
          clearCategoryMethod();
          deleteCategoryView.value = false
          reserveCategory.value = null
        }
      )
    }

    return {
      // store
      storeCategory,
      // variable
      selected,
      filterRow,
      currentCategory,
      reserveCategory,
      windowWidth,
      windowHeight,
      deleteCategoryView,
      // methods
      setCategoryMethod,
      clearFilterMethod,
      clearParentCategoryMethod,
      clearReserveCategoryMethod,
      clearCategoryMethod,
      saveCategoryMethod,
      openDeleteCategoryViewMethod,
      deleteCategoryMethod
    }
  }
})


</script>

<style lang="sass" scoped>
.div-border
  border: 1px solid gray
  border-radius: 4px
</style>
