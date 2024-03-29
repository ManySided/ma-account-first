<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <q-breadcrumbs>
        <q-breadcrumbs-el label="Счёт"/>
        <q-breadcrumbs-el label="Новый - Выбранный"/>
      </q-breadcrumbs>
    </div>

    <div class="q-pa-md example-row-equal-width">
      <div class="row">
        <div class="col">
          <q-toggle v-model="isShowActivity" label="Показывать только активные счета"/>
        </div>
      </div>
      <div class="row">
        <div class="col-md-3">
          <q-list dense bordered padding class="rounded-borders">
            <q-item clickable v-ripple>
              <q-item-section>
                Домашний общий счёт
              </q-item-section>
            </q-item>

            <q-item clickable v-ripple>
              <q-item-section>
                Банковский вклад: ВТБ - доходный
              </q-item-section>
            </q-item>

            <q-item clickable v-ripple>
              <q-item-section>
                Копилка: новая квартира
              </q-item-section>
            </q-item>
          </q-list>
        </div>
        <div class="col-md-8" style="padding-left: 5px">
          <q-card flat bordered class="my-card">
            <q-card-section>
              <div class="q-gutter-y-md column">
                <q-input outlined v-model="fieldTxt" label="Название"/>
                <q-input outlined v-model="fieldTxt" type="textarea" label="Комментарий"/>
                <q-input outlined v-model="fieldTxt" type="number" label="Начальная сумма"/>
                <q-input outlined v-model="fieldTxt" type="number" readonly label="Текущая сумма"/>
                <q-select outlined v-model="fieldTxt" label="Валюта"/>
                <q-input filled v-model="date" mask="date" :rules="['date']" label="Дата создания">
                  <template v-slot:append>
                    <q-icon name="event" class="cursor-pointer">
                      <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                        <q-date v-model="date">
                          <div class="row items-center justify-end">
                            <q-btn v-close-popup label="Close" color="primary" flat/>
                          </div>
                        </q-date>
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                </q-input>
              </div>
            </q-card-section>

            <q-separator dark/>

            <q-card-actions>
              <q-btn flat>Сохранить</q-btn>
              <q-btn flat>Удалить</q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';

export default defineComponent({
  name: 'AccountPage',
  setup() {
    const fieldTxt = ref('');
    const isShowActivity = ref(true);
    const date = ref('2024/01/01');
    /*const accountDto = ref({
      "id": ""
    });*/
    return {isShowActivity, fieldTxt, date}
  }
});
</script>

<style scoped>

</style>
