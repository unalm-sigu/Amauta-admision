<template>
    <div>

        <modal-simple ref="modal_readmision"
                      v-bind:okaction="save" >

            <div slot="header">
                <h4 class="modal-title">Nuevo Trámite Readmisión</h4>
            </div>

            <div slot="body">
                <form data-parsley-validate="true">

                    <div class="form-group">
                        <label>Alumno</label>

                        <multiselect
                            v-model="readmision.alumno"
                            v-bind:options="alumnos"
                            v-bind:allow-empty="true"
                            v-on:search-change="searchAlumno"
                            track-by="id"
                            placeholder=" "
                            label='nombre'
                            v-bind:internal-search="false"
                            v-bind:hide-selected="true"
                            v-bind:showNoOptions="true"
                            v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                <span class="">{{ props.option.persona.nombreCompleto }}</span>
                                <span class="">{{ props.option.persona.tipoDocumento.simbolo }} - {{ props.option.persona.numeroDocIdentidad }}</span>
                            </template>

                            <template slot="option" slot-scope="props">
                                <span class="block bold">{{ props.option.persona.nombreCompleto }} </span>
                                <span class="text-xs">{{ props.option.persona.tipoDocumento.simbolo }} - {{ props.option.persona.numeroDocIdentidad }}</span>
                            </template>

                            <template slot="noOptions">&nbsp</template>
                            <template slot="noResult">&nbsp</template>

                        </multiselect>

                        <input v-model="readmision.alumno" required="true" type="text" class="hide"/>

                    </div>

                    <div class="form-group">
                        <label>Ciclo de readmisión</label>

                        <multiselect
                            v-model="readmision.cicloReadmitido"
                            v-bind:options="ciclos"
                            v-bind:allow-empty="true"
                            track-by="id"
                            placeholder=" "
                            label='descripcion'
                            v-bind:internal-search="true"
                            v-bind:hide-selected="false"
                            v-bind:showNoOptions="true"
                            v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                <span class="">{{ props.option.descripcion }}</span>
                            </template>

                            <template slot="option" slot-scope="props">
                                <span class="">{{ props.option.descripcion }} </span>
                            </template>

                            <template slot="noOptions">&nbsp</template>
                            <template slot="noResult">&nbsp</template>

                        </multiselect>

                        <input v-model="readmision.cicloReadmitido" required="true" type="text" class="hide"/>

                    </div>

                    <div class="form-group">
                        <label>Motivo</label>
                        <textarea class="form-control" v-model="readmision.motivo" required="true"></textarea>
                    </div>

            </div>
        </modal-simple> 

    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                readmision: {},
                ciclos: JSON.parse(CICLOS),
                ciclo: {},
                alumnos: [],
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            open() {
                let $vue = this;
                $vue.readmision = {};
                $vue.$refs.modal_readmision.open();
            },
            save() {
                let $vue = this;
                axios.post(APP.url("academico/tramiteacademico/readmision"), $vue.readmision)
                        .then(({data}) => {

                            if (data.success) {
                                $vue.$parent.$parent.$refs.readmisionlist.recargar();
                                notify(data.message, "success");
                            } else {
                                notify(data.message, "error");
                            }
                            
                            $vue.$refs.modal_readmision.close();

                        }, error => {
                            
                            $vue.$refs.modal_readmision.stop();
                            notify(Messages.errorComunicacion, "error");
                        });
            },
            searchAlumno(nombre) {

                let $vue = this;

                if (nombre) {

                    axios.get(APP.url("academico/tramiteacademico/readmision/searchAlumno"), {params: {nombre: nombre}})
                            .then(({data}) => {

                                if (data.success) {
                                    $vue.alumnos = data.data;
                            }

                            });

                }
            }
        }
    };
</script>