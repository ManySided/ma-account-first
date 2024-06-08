<template>
  <q-page>
    <div class="row">
      <div class="column" v-for="(item, index) in storeAccount.getActualAccounts" :key="index" style="padding: 10px">
        <q-card class="my-card">
          <q-card-section>
            <div class="text-h6">{{ item.name }}</div>
            <div class="text-subtitle2">{{ formattedNumber(item.currentSum) }}
              <q-icon :name="item.currency.symbol"/>
            </div>
          </q-card-section>

          <q-separator/>

          <q-card-actions vertical>
            <q-btn flat @click="$router.push({name:'tickets', params: {accountId: item.id}})">
              Опреации
            </q-btn>
          </q-card-actions>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import {useAccountStore} from 'stores/accountStore';

export default defineComponent({
  name: 'DashboardPage',
  setup() {
    // init
    const storeAccount = useAccountStore();

    // load data
    storeAccount.loadAccounts()

    // methods
    const formattedNumber = (n: number) => {
      if (n)
        return n.toLocaleString();
      return '';
    }
    return {
      storeAccount,
      formattedNumber
    }
  }
});
</script>

<style lang="sass" scoped>
.my-card
  width: 100%
  max-width: 250px
</style>
