<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated style="background-color: #292961">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar square>
            <q-img src="~assets/logo.png"></q-img>
          </q-avatar>
          <b>Arving</b>
        </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view></router-view>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">

defineOptions({
  name: 'MainLayout'
});

</script>
