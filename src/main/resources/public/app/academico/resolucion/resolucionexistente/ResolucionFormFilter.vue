<template>
    <div>
        <div>
            <div class="col-lg-6">
                <label>Filtro Facultad</label>
                <div class="form-group">
                    <multiselect 
                        v-model="oficinaLocal" 
                        v-bind:options="oficinas"
                        placeholder=" "
                        label="nombre"
                        v-bind:allow-empty="true"
                        track-by="id" 
                        v-on:select="oficinaSelect"
                        v-on:remove="oficinaRemove"
                        required="true" >
                    </multiselect>
                </div>
            </div> 
            <div class="col-lg-6">
                <label>Filtro Visualizar Solo Seleccionados</label>
                <div class="form-group">
                    <input type="checkbox" style="width: 25px;height: 25px;" 
                           v-on:click="changeVisualizar()" class="v-middle" 
                           v-model="seleccionado"/> 
                </div>
            </div>
        </div>

    </div>
</template>

<script>
    module.exports = {
        props: {
            callfilter: {type: Function, default: () => {}},
        },
        data() {
            return {
                oficinas: JSON.parse(oficinasJson),
                oficinaLocal: null,
                seleccionadoLocal:false,
                facultad:null,
                seleccionado:false
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            changeVisualizar() {
                let $vue = this;
                $vue.seleccionado=!$vue.seleccionado;
                $vue.callfilter($vue.facultad,$vue.seleccionado);
            },
            oficinaSelect(item) {
                let $vue = this;
                $vue.facultad = item.instanciaOficina;
                $vue.callfilter($vue.facultad,$vue.seleccionado);
            },
            oficinaRemove() {
                let $vue = this;
                $vue.facultad = null;
                $vue.callfilter(null,$vue.seleccionado);
            },
        }
    };
</script>