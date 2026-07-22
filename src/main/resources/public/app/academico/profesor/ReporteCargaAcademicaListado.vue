<template>
    <div>

        <modal-simple ref="modalReporteCargaAcademicaListado"
                      v-bind="reporteCargaAcademicaListadoModal"
                      v-bind:okaction="downloadReporteCargaAcademicaListado">
            <div slot="header">
                <p class="h4 text-primary">Reporte Carga Académica por Listado</p>
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
                    <label class="bold">Docentes</label>

                    <multiselect
                        v-model="docentes"
                        v-bind:options="docentesOpciones"
                        v-on:search-change="searchDocente"
                        placeholder="Buscar docente..."
                        label="nombre"
                        internal
                        v-bind:multiple="true"
                        v-bind:allow-empty="true"
                        deselect-label=" "
                        select-label=" "
                        v-bind:internal-search='false'
                        track-by="id"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="tag" slot-scope="props">
                            <span class="multiselect__tag">
                                <span>{{ props.option.persona.nombreCompleto }}</span>
                                <i tabindex="1" class="multiselect__tag-icon" v-on:click="props.remove(props.option)"></i>
                            </span>
                        </template>
                        <template slot="option" slot-scope="props">
                            <span>{{ props.option.persona.nombreCompleto }}</span>
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
        components: {
            ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        },
        data() {
            return {
                ciclos: JSON.parse(jCicloAcademicosNivelacion),
                grados: [{id: 'PRE', nombre: 'Solo pregrado'}, {id: 'EPG', nombre: 'Solo posgrado'}],
                docentesOpciones: [],
                ciclo: null,
                tipoGrado: null,
                docentes: []
            };
        },
        mounted: function () {
        },
        methods: {
            open() {
                let vue = this;
                vue.ciclo = null;
                vue.tipoGrado = null;
                vue.docentes = [];
                vue.docentesOpciones = [];
                vue.$refs.modalReporteCargaAcademicaListado.open();
            },
            downloadReporteCargaAcademicaListado() {
                let vue = this;
                if (!vue.docentes || vue.docentes.length === 0) {
                    notify('Seleccione al menos un docente', 'warning');
                    vue.$refs.modalReporteCargaAcademicaListado.stop();
                    return;
                }

                const descargarPorDocente = (index) => {
                    if (index >= vue.docentes.length) {
                        vue.$refs.modalReporteCargaAcademicaListado.close();
                        return;
                    }
                    const docente = vue.docentes[index];
                    const data = {
                        tipoGrado: vue.tipoGrado ? vue.tipoGrado.id : '',
                        cicloAcademicos: vue.ciclo,
                        docente: docente.id
                    };
                    axios_blob.post("/academico/profesor/reporteCargaAcademica", data)
                            .then(response => {
                                UTIL_BLOB.save(response);
                                setTimeout(() => descargarPorDocente(index + 1), 800);
                            }, () => {
                                vue.$refs.modalReporteCargaAcademicaListado.stop();
                                notify(Messages.errorComunicacion, 'error');
                            });
                };

                descargarPorDocente(0);
            },
            searchDocente(nombre) {
                let $vue = this;
                if (!nombre) {
                    return;
                }
                axios.get(APP.url("academico/profesor/searchDocente"),
                        {params: {nombre: nombre}})
                        .then(response => {
                            $vue.docentesOpciones = response.data;
                        });
            }
        }
    };
</script>
