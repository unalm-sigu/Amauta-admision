<template>
    <div>

        <div class="pull-right m-t-md">  
            <a class="btn btn-info" type="button" v-on:click.prevent="nuevo" href="#" >Nuevo</a>
        </div> 
        
        <readmision-form ref="readmisionform"></readmision-form>

    </div>
</template>

<script>

    const ReadmisionForm = httpVueLoader('/app/academico/tramitesacademicos/readmision/ReadmisionForm.vue');

    module.exports = {
        components: {
            readmisionForm: ReadmisionForm,
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
                this.$refs.readmisionform.open();
            }
        }
    };
</script>