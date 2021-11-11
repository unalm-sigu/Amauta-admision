<template>
    <div>
    </div>
</template>

<script>
    module.exports = {
        mixins: [VueLoader],
        data() {
            return {
            };
        },
        mounted: function () {
        },
        methods: {
            open() {
                let $vue = this;
                $vue.showLoader();
                axios_blob.post("/academico/profesor/reporteContratoLaboral")
                        .then(response => {
                            UTIL_BLOB.save(response);
                            $vue.hideLoader();
                        }, () => {
                            $vue.hideLoader();
                            notify(Messages.errorComunicacion, 'error')
                        });
            }
        }
    };
</script>