<template>
    <div>

        <modal-simple ref="modalReporteCargaAcademica"
                      v-bind="reporteCargaAcademicaModal"
                      v-bind:okaction="downloadReporteCargaAcademica">
            <div slot="header">
                <p class="h4 text-primary">Reporte Carga Acádemica</p>
            </div>
            <div slot="body">

                <div class="form-group m-b-xs">
                    <label class="bold">Ciclo Académico</label>

                    <multiselect v-model='ciclo'
                                 label='descripcion'
                                 placeholder="Seleccione el ciclo académico"
                                 deselect-label=" "
                                 select-label=" "
                                 v-bind:multiple="true"
                                 track-by='id'
                                 v-bind:allow-empty="true"
                                 v-bind:options='ciclos'>
                    </multiselect>
                </div>

                <div class="form-group m-b-xs">
                    <label class="bold">Facultad</label>

                    <multiselect v-model='facultad'
                                 label='nombre'
                                 placeholder="Todas las facultades"
                                 deselect-label=""
                                 select-label=""
                                 track-by='id'
                                 v-on:input="changeFacultad"
                                 v-bind:allow-empty="true"
                                 v-bind:options='facultades'>
                    </multiselect>
                </div>
                <div class="form-group m-b-xs">
                    <label class="bold">Departamento Acádemico</label>

                    <multiselect v-model='departamento'
                                 label='nombre'
                                 placeholder="Todos los departamentos académicos"
                                 deselect-label=""
                                 select-label=""
                                 track-by='id'
                                 v-bind:allow-empty="true"
                                 v-bind:options='departamentosSelectos'>
                    </multiselect>
                </div>
                <div class="form-group m-b-xs">
                    <label class="bold"></label>

                    <multiselect v-model='tipoGrado'
                                 label='nombre'
                                 placeholder="Pregrado y posgrado"
                                 deselect-label=""
                                 select-label=""
                                 track-by='id'
                                 v-bind:allow-empty="true"
                                 v-bind:options='grados'>
                    </multiselect>
                </div>

                <div class="form-group m-b-xs">
                    <label class="bold">Docente</label>

                    <multiselect 
                        v-model="docente" 
                        v-bind:options="docentes"
                        v-on:search-change="searchDocente"
                        placeholder=" "
                        label="nombre"
                        internal
                        v-bind:allow-empty="true"
                        deselect-label=" "
                        v-bind:internal-search='false'
                        track-by="id" 
                        required="true"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false" >

                        <template slot="singleLabel" slot-scope="props">
                            <span class=""> {{ props.option.persona.nombreCompleto }}</span>
                        </template>
                        <template slot="option" slot-scope="props">
                            <span class=""> {{ props.option.persona.nombreCompleto }}</span>
                        </template>

                        <template slot="noOptions">&nbsp;</template>
                        <template slot="noResult">&nbsp;</template>

                    </multiselect>

                </div>

            </div>
        </modal-simple>

    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                facultades: JSON.parse(jFacultades),
                departamentos: JSON.parse(jDepartamentos),
                departamentosSelectos: [],
                docentes: [],
                ciclos: JSON.parse(jCicloAcademicos),
                grados: [{id: 'PRE', nombre: 'Solo pregrado'}, {id: 'EPG', nombre: 'Solo posgrado'}],
                ciclo: null,
                facultad: null,
                departamento: null,
                tipoGrado: null,
                docente: null
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            open() {
                let vue = this;
                vue.ciclo = null;
                vue.facultad = null;
                vue.departamento = null;
                vue.tipoGrado = null;
                vue.docente = null;
                vue.$refs.modalReporteCargaAcademica.open();
            },
            downloadReporteCargaAcademica() {
                let vue = this;
                let data = {
                        departamento: vue.departamento ? vue.departamento.id : '',
                        tipoGrado: vue.tipoGrado ? vue.tipoGrado.id : '',
                        facultad: vue.facultad ? vue.facultad.id : '',
                        cicloAcademicos: vue.ciclo,
                        docente: vue.docente ? vue.docente.id : '',
                    };
                axios_blob.post("/academico/profesor/reporteCargaAcademica", data)
                        .then(response => {
                            UTIL_BLOB.save(response);
                            vue.$refs.modalReporteCargaAcademica.close();
                        }, () => {
                            vue.$refs.modalReporteCargaAcademica.stop();
                            notify(Messages.errorComunicacion, 'error')
                        });
            },
            changeFacultad() {
                let $vue = this;
                $vue.departamentosSelectos = [];
                $vue.departamento = null;
                if ($vue.facultad) {
                    $vue.departamentos.map((x, i) => {
                        if (x.facultad.id == $vue.facultad.id) {
                            $vue.departamentosSelectos.push(x)
                        }
                    });
                    $vue.departamentosSelectos;
                }
            },
            searchDocente(nombre) {
                let $vue = this;
                if (!nombre) {
                    return;
                }
                axios.get(APP.url("academico/profesor/searchDocente"),
                        {params: {nombre: nombre}})
                        .then(response => {
                            $vue.docentes = response.data;
                        });
            }
        }
    };
</script>