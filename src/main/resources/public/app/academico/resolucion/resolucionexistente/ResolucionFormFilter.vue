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
                        required="true" >
                    </multiselect>
                </div>
            </div> 
            <div class="col-lg-6">
                <label>Filtro Visualizar Solo Seleccionados</label>
                <div class="form-group">
                    <input type="checkbox" style="width: 25px;height: 25px;" v-on:click="changeVisualizar()" class="v-middle" v-model="visualizarSoloSeleccionados"/> 
                </div>
            </div>
        </div>

    </div>
</template>

<script>
    module.exports = {
        computed: {
            ...Vuex.mapState(["filterFacultad", "visualizarSoloSeleccionados"]),
        },
        data() {
            return {
                oficinas: JSON.parse(oficinasJson),
                oficinaLocal: null,
                visualizarSoloSeleccionadosLocal: false
            };
        },
        mounted: function () {
            let $vue = this;
            $vue.oficinaLocal = {...$vue.filterFacultad};
        },
        methods: {
            ...Vuex.mapActions(['toggleSeleccionado','setFilterFacultad']),
            changeVisualizar() {
                let $vue = this;
                $vue.toggleSeleccionado();
            },
            oficinaSelect(item) {
                let $vue = this;
                $vue.setFilterFacultad(item);
                $vue.$forceUpdate();
            }
        }
    };
</script>