<template>
    <div>

        <div class="pull-right m-t-md">  
            <a class="btn btn-info" type="button" v-on:click.prevent="nuevo" href="#" >Nuevo</a>
        </div> 
        
        <cambio-plan-curricular-form ref="cambioplancurricularform"></cambio-plan-curricular-form>

    </div>
</template>

<script>

    const CambioPlanCurricularForm = httpVueLoader('/app/academico/tramitesacademicos/cambioplancurricular/CambioPlanCurricularForm.vue');

    module.exports = {
        components: {
            cambioPlanCurricularForm: CambioPlanCurricularForm,
        },
        data() {
            return {
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            nuevo() {
                this.$refs.cambioplancurricularform.open();
            }
        }
    };
</script>