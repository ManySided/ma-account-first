<template>
  <div class="row">
    <div class="col-7 field-padding">
      <q-input
        outlined
        label="Название"
        v-model="operationVariable.name"
        hide-bottom-space
        :error="!isValidName"
        error-message="Не заполнено поле название операции">
        <template v-slot:append>
          <q-icon name="manage_search" class="cursor-pointer">
            <q-popup-proxy cover ref="popup-proxy">
              <div style="width: 400px; height: 300px; padding: 5px">
                <q-input ref="filterRef" outlined v-model="filterRow" label="Фильтр" @keyup.enter="findLikeOperations">
                  <template v-slot:append>
                    <q-icon v-if="filterRow.length>0" name="search" class="cursor-pointer" @click="findLikeOperations"/>
                    <q-icon v-if="filterRow!==''" name="clear" class="cursor-pointer" @click="clearOperationFilter"/>
                  </template>
                </q-input>
                <div style="padding-bottom: 5px">
                  <p
                    align="center"
                    v-if="!operationStore.getLikedGroups.length">
                    Список подходящих операций
                  </p>
                </div>
                <q-list v-if="Array.isArray(operationStore.getLikedGroups) && operationStore.getLikedGroups.length"
                        bordered separator>
                  <q-item clickable v-ripple v-for="(itemOperation, indexOperation) in operationStore.getLikedGroups"
                          :key="indexOperation">
                    <q-item-section @click="getOperationByName(itemOperation)">
                      <!--@click.once="$refs['popup-proxy'].hide()"-->
                      {{ itemOperation }}
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
            </q-popup-proxy>
          </q-icon>
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
  <div class="row" v-if="showPurchaseEdit">
    <p align="center"><i>тут будет редактирование товара</i></p>
  </div>
</template>

<script setup lang="ts">
import {computed, ref} from 'vue';
import CustomFieldDate from 'components/utils/CustomFieldDate.vue';
import CategoryTreeModal from 'components/utils/CategoryTreeModal.vue';
import {useOperationStore} from 'stores/operationStore';
import {Operation} from 'src/model/dto/TicketDto';

defineOptions({
  name: 'OperationEditRow',
  components: {CustomFieldDate, CategoryTreeModal},
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

// variables
const filterRow = ref('')
const filterRef = ref(null)
const showComment = ref(false);
const showPurchaseEdit = ref(false);

// methods
const changeShowComment = () => {
  showComment.value = !showComment.value
}
const changeShowPurchaseEdit = () => {
  showPurchaseEdit.value = !showPurchaseEdit.value
}
const clearOperationFilter = () => {
  filterRow.value = ''
}
const findLikeOperations = () => {
  operationStore.actionFindLikedGroups(filterRow.value, props.accountId);
}
const getOperationByName = (selectedName: string) => {
  if (operationVariable.value) {
    operationStore.actionFindLastOperationByNameAndFill(selectedName, props.accountId, operationVariable.value)
  }
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
