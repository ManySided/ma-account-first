<template>
  <div class="row">
    <div class="col-7 field-padding">
      <q-input
        outlined
        label="Название"
        v-model="operationVariable.name"
        hide-bottom-space
        :error="!isValidName"
        error-message="Не заполнено поле название операции"
        @keydown.enter.prevent="searchAndShowVariants"
      >
        <template v-slot:append>
          <q-icon name="chat_bubble_outline" class="cursor-pointer" @click="changeShowComment"/>
          <q-icon name="shopping_cart" class="cursor-pointer" @click="changeShowPurchaseEdit"/>
        </template>
      </q-input>
    </div>

    <div class="col-3 field-padding">
      <category-tree-modal v-model:categoryVariable="operationVariable.category" :account-id="accountId"/>
    </div>
    <div class="col-2 field-padding">
      <q-input
        outlined
        label="Сумма"
        v-model="operationVariable.sum"
        @change="$emit('changeSum')"
        type="number"
        hide-bottom-space
        :error="!isValidSum"
        error-message="Не заполнено поле суммы операции"/>
    </div>
  </div>
  <div class="row" v-if="showComment">
    <div class="col-12 field-padding">
      <q-input
        outlined
        label="Комментарий"
        autogrow
        v-model="operationVariable.comment"
        hide-bottom-space/>
    </div>
    <div class="col-1"></div>
  </div>
  <!-- Теги операции -->
  <div class="row" v-if="!operationVariable.id">
    <q-btn flat color="primary" label="добавить тег" size="sm">
      <q-popup-proxy>
        <div class="q-pa-md" style="max-width: 350px">
          <q-list>
            <q-item>
              <q-item-section>
                <q-input v-model="tagName" label="Название тега" @keydown.enter.prevent="addTag(operationVariable)"/>
              </q-item-section>
              <q-item-section avatar>
                <q-icon color="primary" name="add" @click="addTag(operationVariable)"/>
              </q-item-section>
            </q-item>
            <!-- Временный список тегов. Созданные во время занесения данных -->
            <div v-if="tagStore.getHasTempTag">
              <div v-for="(itemGlobalTag, indexGlobalTag) in tagStore.getTempTags" :key="indexGlobalTag">
                <q-item
                  clickable
                  @click="addTagByItem(operationVariable, itemGlobalTag)"
                  v-if="isLikeTag(itemGlobalTag)">
                  <q-item-section>#{{ itemGlobalTag.name }}</q-item-section>
                </q-item>
              </div>
              <q-separator/>
            </div>
            <div v-for="(itemGlobalTag, indexGlobalTag) in tagStore.getTags" :key="indexGlobalTag">
              <q-item
                clickable
                @click="addTagByItem(operationVariable, itemGlobalTag)"
                v-if="isLikeTag(itemGlobalTag)">
                <q-item-section>#{{ itemGlobalTag.name }}</q-item-section>
              </q-item>
            </div>
          </q-list>
        </div>
      </q-popup-proxy>
    </q-btn>
    <div v-for="(itemTag, indexTag) in operationVariable.tags" :key="indexTag">
      <q-chip dense
              clickable
              color="primary"
              text-color="white"
              icon-right="close"
              @click="removeTag(operationVariable, itemTag)">
        {{ itemTag.name }}
      </q-chip>
    </div>
  </div>
  <div class="row" v-if="showPurchaseEdit">
    <p align="center"><i>тут будет редактирование товара</i></p>
  </div>

  <q-dialog v-model="showSearchLikedGroups">
    <div class="row">
      <q-list separator class="bg-blue-grey-3 text-black shadow-2">
        <q-item
          clickable
          v-for="(itemOperation, indexOperation) in operationStore.getLikedGroups" :key="indexOperation"
          @click="getOperationByName(itemOperation)"
        >
          {{ itemOperation }}
        </q-item>
      </q-list>
    </div>
  </q-dialog>
</template>

<script setup lang="ts">
import {computed, ref} from 'vue';
import CategoryTreeModal from 'components/utils/CategoryTreeModal.vue';
import {useOperationStore} from 'stores/operationStore';
import {Operation, OperationTag} from 'src/model/dto/TicketDto';
import {useTagStore} from 'stores/tagsStore';

defineOptions({
  name: 'OperationEditRow',
  components: {CategoryTreeModal},
});
const props = defineProps(['accountId']);
const operationVariable = defineModel<Operation>('operationVariable', {
  default: {
    sum: 0,
    name: ''
  }
})

// store
const operationStore = useOperationStore();
const tagStore = useTagStore();

// variables
const showComment = ref(false);
const showPurchaseEdit = ref(false);
const showSearchLikedGroups = ref(false);
const tagName = ref('')

// methods
const changeShowComment = () => {
  showComment.value = !showComment.value
}
const changeShowPurchaseEdit = () => {
  showPurchaseEdit.value = !showPurchaseEdit.value
}
const getOperationByName = (selectedName: string) => {
  if (operationVariable.value) {
    operationStore.actionFindLastOperationByNameAndFill(selectedName, props.accountId, operationVariable.value)
  }
  showSearchLikedGroups.value = false;
}
const searchAndShowVariants = () => {
  operationStore.actionFindLikedGroups(operationVariable.value.name, props.accountId);
  showSearchLikedGroups.value = true;
}
const addTag = (operation: Operation) => {
  const tag: OperationTag = {
    name: tagName.value
  };
  if (operation.tags)
    operation.tags.push(tag)
  else {
    operation.tags = []
    operation.tags.push(tag)
  }
  tagName.value = ''
  addTempTag(tag)
}
const addTagByItem = (operation: Operation, tag: OperationTag) => {
  if (!operation.tags)
    operation.tags = []

  operation.tags.push(tag)
  tagName.value = ''
  addTempTag(tag)
}
const addTempTag = (tag: OperationTag) => {
  console.log('проверим id тега')
  if (!tag.id) {
    console.log('добавили временный тег')
    tagStore.actionAddTempTag(tag)
  }
}
const removeTag = (operation: Operation, tag: OperationTag) => {
  const tagIndex = operation.tags?.indexOf(tag, 0)
  if (tagIndex || tagIndex === 0) {
    operation.tags?.splice(tagIndex, 1)
  }
}
const isLikeTag = (tag: OperationTag) => {
  return tag.name.includes(tagName.value)
}
// validate
const isValidName = computed(() => {
  if (operationVariable.value) {
    return operationVariable.value.name.length > 0
  }
  return false;
})
const isValidSum = computed(() => {
  if (operationVariable.value) {
    return operationVariable.value.sum && Number(operationVariable.value.sum) > 0
  }
  return false;
})
</script>

<style lang="sass" scoped>
.field-padding
  padding-right: 5px
  padding-bottom: 5px
</style>
