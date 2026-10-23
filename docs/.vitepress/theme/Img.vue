<script setup lang="ts">
const props = defineProps<{ src: string, alt?: string }>();

function openDialog(event) {
  event.currentTarget.nextElementSibling.showModal();
}

function closeDialog(event) {
  event.currentTarget.close();
}
</script>
<template>
  <div>
    <img
      :src="src"
      :alt="alt"
      @click="openDialog"
    />
    <dialog @click="closeDialog">
      <img :src :alt />
    </dialog>
  </div>
</template>
<style scoped>
img {
  cursor: zoom-in;
  max-width: 100%;
  height: auto;
}

dialog {
  border: none;
  cursor: zoom-out;
  outline: none;
}

dialog img {
  cursor: zoom-out;
  max-height: 90vh;
  max-width: 90vw;
}
</style>