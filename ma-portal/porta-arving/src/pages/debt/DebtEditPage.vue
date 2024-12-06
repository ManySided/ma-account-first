<template>
  <q-page>
    <div class="q-pa-md example-row-equal-width">
      <div class="row justify-center">
        <div class="col-md-8" style="padding-left: 5px">
          <q-card flat bordered>
            <q-card-section class="q-pt-xs">
              <!-- Секция чека -->
              <q-btn flat color="primary" @click="leavePage">
                <q-icon left name="arrow_back"/>
                Вернуться
              </q-btn>
            </q-card-section>

            <q-card-section>
              <div class="q-gutter-y-md column">
                <q-input
                  outlined
                  v-model="storeDebt.getCurrentDebt.name"
                  label="Название"
                  hide-bottom-space
                  :error="!isValidFieldName"
                  error-message="Название долга должно быть заполнено"/>
                <q-input outlined v-model="storeDebt.getCurrentDebt.description" type="textarea" label="Описание"/>
                <custom-field-date
                  v-model:dateVariable="storeDebt.getCurrentDebt.createDate"
                  field-name="Дата создания"
                  can-change/>
                <q-input outlined v-model="storeDebt.getCurrentDebt.sumDebt"
                         type="number"
                         @readonly="!isCreate"
                         min="0"
                         :error="!isValidFieldSum"
                         error-message="Начальная сумма должна быть заполнена"
                         hide-bottom-space
                         label="Сумма долга"/>
                <q-input outlined v-model="storeDebt.getCurrentDebt.sumDebtCurrent"
                         type="number"
                         readonly
                         label="Текущая сумма"/>
                <q-checkbox v-model="storeDebt.getCurrentDebt.debtorFlag">
                  Должник
                  <q-tooltip>
                    При указание галочки, будет считаться, что долг принадлежит Вам и Вы его должны отдавать
                  </q-tooltip>
                </q-checkbox>
              </div>
            </q-card-section>
            <q-card-section v-if="!isCreate">
              <q-list bordered separator>
                <q-item-label header>Этапы погашения</q-item-label>
                <div v-for="(itemOperation, indexOperation) in storeDebt.getCurrentDebt.debtOperations"
                     :key="indexOperation">
                  <q-item clickable v-ripple v-if="!itemOperation.firstOperation">
                    <q-item-section>
                      {{ formattedDate(itemOperation.ticket.date) }}
                    </q-item-section>
                    <q-item-section>
                      <div style="text-align: right">{{ formattedNumber(itemOperation.sumOperation) }}
                        {{ storeStuff.getAccountCurrencyShortName }}
                      </div>
                    </q-item-section>
                    <q-item-section side>
                      <q-icon name="delete_outline" class="cursor-pointer">
                        <q-tooltip>
                          Удалить операцию
                        </q-tooltip>
                      </q-icon>
                    </q-item-section>
                  </q-item>
                </div>
                <q-item>
                  <q-item-section>
                  </q-item-section>
                  <q-item-section side>
                    <add-debt
                      v-model:debt-id="storeDebt.currentDebt.id"
                      @add-event="refreshAfterAddOperation"/>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-card-section>
            <q-card-section v-if="isCreate">
              <q-btn color="primary" @click="validateBeforeSaveDebt">
                Сохранить
              </q-btn>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script lang="ts">
import {computed, defineComponent, ref} from 'vue';
import {useStuffStore} from 'stores/stuffStore';
import {useDebtStore} from 'stores/debtStore';
import CustomFieldDate from 'components/utils/CustomFieldDate.vue';
import {date, Notify} from 'quasar';
import {useRouter} from 'vue-router';
import {isValidDebt, isValidName, isValidSum} from 'src/model/dto/DebtDto';
import AddDebt from 'components/utils/debt/AddDebt.vue';

export default defineComponent({
  name: 'DebtEditPage',
  components: {CustomFieldDate, AddDebt},
  props: ['debtId'],
  setup(props) {
    // store
    const storeStuff = useStuffStore();
    const storeDebt = useDebtStore();

    // init
    if (props.debtId)
      storeStuff.actionUpdateTitlePage('Редактирование долга');
    else storeStuff.actionUpdateTitlePage('Создание долга');

    storeDebt.actionInitDebt(props.debtId)

    const router = useRouter();
    const windowWidth = ref(window.innerWidth)
    const windowHeight = ref(window.innerHeight)
    const id = ref(props.debtId)
    const isCreate = ref(true);

    if (props.debtId)
      isCreate.value = false

    // validate methods
    const isValidFieldName = computed(() => isValidName(storeDebt.getCurrentDebt))
    const isValidFieldSum = computed(() => isValidSum(storeDebt.getCurrentDebt))

    const formattedNumber = (n: number) => {
      if (n)
        return n.toLocaleString();
      return '';
    }
    const formattedDate = (d: string) => {
      if (d) {
        const cloneDate = date.extractDate(d, 'YYYY-MM-DD')
        return date.formatDate(cloneDate, 'DD.MM.YYYY');
      }
      return '';
    }
    const leavePage = () => {
      router.push({name: 'debts'})
    }
    const validateBeforeSaveDebt = () => {
      // произвести валидацию
      if (isValidDebt(storeDebt.getCurrentDebt)) {
        saveDebt()
      } else {
        // сообщить о незаполненных полях
        Notify.create({
          color: 'negative',
          position: 'top',
          message: 'Валидация формы не пройдена',
          icon: 'report_problem'
        })
      }
    }
    const saveDebt = () => {
      storeDebt.actionSaveDebt(
        storeStuff.getAccountId,
        () => leavePage()
      )
    }
    const refreshAfterAddOperation = () => {
      storeDebt.actionInitDebt(props.debtId)
    }

    return {
      // store
      storeStuff,
      storeDebt,
      // variable
      id,
      isCreate,
      windowWidth,
      windowHeight,
      // methods
      formattedNumber,
      formattedDate,
      leavePage,
      validateBeforeSaveDebt,
      saveDebt,
      refreshAfterAddOperation,
      // validate
      isValidFieldName,
      isValidFieldSum
    }
  }
})
</script>

<style lang="sass" scoped>

</style>
