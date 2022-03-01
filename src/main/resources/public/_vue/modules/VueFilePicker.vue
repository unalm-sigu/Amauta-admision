<template>
    <div>
        <button type="button" class="btn btn-primary" v-on:click="openPicker">
            <i class="fa fa-cloud-upload"></i> &nbsp;
            <slot name="nombre">
                {{nombre}}
            </slot>
        </button>
    </div>
</template>
<script type="text/javascript">
    module.exports = {
        props: {
            onpick: {type: Function, default: () => {
                }},
            nombre: {type: String, default: 'Subir Archivo'},
        },
        data() {
            return {

            }
        },
        methods: {
            openPicker() {
                let $vue = this;
                const chooser = document.createElement('input');
                chooser.type = 'file';
                chooser.onchange = (e) => {
                    $vue.onpick(e);
                };
                chooser.click();
                delete chooser;
            }
        }
    }

</script>