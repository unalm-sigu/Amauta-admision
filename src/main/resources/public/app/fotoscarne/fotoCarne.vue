<template>
    <div>

        <download-foto-carne></download-foto-carne>

        <upload-foto-carne></upload-foto-carne>

    </div>
</template>

<script>

    const DownloadFotoCarne = httpVueLoader('/app/fotoscarne/downloadFotoCarne.vue');
    const UploadFotoCarne = httpVueLoader('/app/fotoscarne/uploadFotoCarne.vue');

    module.exports = {
        components: {
            DownloadFotoCarne: DownloadFotoCarne,
            UploadFotoCarne: UploadFotoCarne,
        }
    };
</script>