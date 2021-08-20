<template>
    <div>

        <div v-if="isTitulo ||  isBachiller || isRetiroCiclo || isReincorporacion">
            <div class="col-lg-6">
                <label>Filtro Facultad</label>
                <div class="form-group">
                    <multiselect 
                        v-model="filterFacultad" 
                        v-bind:options="oficinas"
                        placeholder=" "
                        label="nombre"
                        v-bind:allow-empty="true"
                        track-by="id" 
                        required="true"
                        >
                    </multiselect>
                </div>
            </div> 
            <div class="col-lg-6">
                <label>Filtro Visualizar Solo Seleccionados</label>
                <div class="form-group">
                    <input type="checkbox" style="width: 25px;height: 25px;" class="v-middle" v-model="visualizarSelect"/> 
                </div>
            </div>
        </div>
        
    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
        }
    };
</script>