<template>
    <div>

        <download-foto-carne></download-foto-carne>

        <upload-foto-carne></upload-foto-carne>

        <download-lote-foto-carne></download-lote-foto-carne>

    </div>
</template>

<script>

    const DownloadLoteFotoCarne = httpVueLoader('/app/fotoscarne/downloadLoteFotoCarne.vue');
    const DownloadFotoCarne = httpVueLoader('/app/fotoscarne/downloadFotoCarne.vue');
    const UploadFotoCarne = httpVueLoader('/app/fotoscarne/uploadFotoCarne.vue');

    module.exports = {
        components: {
            DownloadLoteFotoCarne: DownloadLoteFotoCarne,
            DownloadFotoCarne: DownloadFotoCarne,
            UploadFotoCarne: UploadFotoCarne,
        }
    };
</script>