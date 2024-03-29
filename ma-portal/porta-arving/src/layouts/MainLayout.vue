<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-toolbar-title>
          Arving
        </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <div class="q-pa-md row items-start q-gutter-md">
        <q-card class="my-card">
          <q-card-section>
            Добро пожаловать
            <q-btn flat @click="$router.push('workspace/account')" label="Счета"/>
          </q-card-section>
        </q-card>
      </div>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">

defineOptions({
  name: 'MainLayout'
});

</script>
