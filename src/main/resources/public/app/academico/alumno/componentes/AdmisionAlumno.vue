<template>
    <div>

        <section class="panel-body">
            <h3 class="m-t-n" style="display: inline-block">Información del proceso de admisión</h3>

            <div v-if="!alumno.postulantePregrado.id" class="alert alert-danger">
                <h3 class="m-t-xs">Este alumno no tiene información de este proceso</h3>
            </div>

            <template v-if="alumno.postulantePregrado.id">
                <div v-if="evaluado.postulante" class="block text-primary bold h4">
                    Ciclo ingreso: {{evaluado.postulante.cicloPostula.cicloAcademico.descripcion}}
                </div>

                <div class="row">
                    <div class="col-md-12">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th class="v-middle" rowspan="2">Tema examen</th>
                                    <th class="v-middle text-center b-r b-l b-light" colspan="2">Examen Admisión</th>
                                    <th class="v-middle text-center b-r b-l b-light" colspan="3">Proceso Nivelación</th>
                                    <th class="v-middle text-center b-r b-l b-light" colspan="3">Historial Académico</th>
                                </tr>
                                <tr>
                                    <th class="v-middle text-center b-l b-light">Puntaje</th>
                                    <th class="v-middle text-center">Nota</th>

                                    <th class="v-middle text-center b-l b-light">Ciclo</th>
                                    <th class="v-middle">Curso</th>
                                    <th class="v-middle text-center">Nota</th>

                                    <th class="v-middle text-center b-l b-light">Ciclo</th>
                                    <th class="v-middle">Curso</th>
                                    <th class="v-middle text-center">Nota</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="item in temas">
                                    <td class="v-middle">{{item.temaExamen.nombre}}</td>
                                    <td class="v-middle text-center"
                                        v-bind:class="classNota(item.nota)">
                                        <span v-if="item.puntaje">
                                            {{score(item.puntaje,4)}}
                                        </span>
                                    </td>
                                    <td class="v-middle text-center"
                                        v-bind:class="classNota(item.nota)">
                                        <span v-if="item.nota">
                                            {{score(item.nota,4)}}
                                        </span>
                                    </td>

                                    <td class="v-middle text-center">
                                        <div v-if="item.cicloNiv"
                                             v-bind:class="classColorNota(item.notaNiv)">
                                            {{item.cicloNiv.descripcion}}
                                        </div>
                                    </td>
                                    <td class="v-middle">
                                        <div v-if="item.cursoNiv"
                                             v-bind:class="classColorNota(item.notaNiv)">
                                            {{item.cursoNiv.codigo}}
                                            {{item.cursoNiv.nombre}}
                                        </div>
                                    </td>
                                    <td class="v-middle text-center">
                                        <div v-if="item.notaNiv.notaCurso" 
                                             v-bind:class="classColorNota(item.notaNiv)">
                                            {{commas(item.notaNiv.notaCurso)}}
                                        </div>
                                    </td>

                                    <td class="v-middle text-center">
                                        <div v-if="item.notaHisto"
                                             v-bind:class="classColorNota(item.notaHisto)">
                                            {{item.cicloHisto.descripcion}}
                                        </div>
                                    </td>
                                    <td class="v-middle">
                                        <div v-if="item.notaHisto"
                                             v-bind:class="classColorNota(item.notaHisto)">
                                            {{item.cursoHisto.codigo}}
                                            {{item.cursoHisto.nombre}}
                                        </div>
                                    </td>
                                    <td class="v-middle text-center">
                                        <div v-if="item.notaHisto"
                                             v-bind:class="classColorNota(item.notaHisto)">
                                            {{item.notaHisto.nota}}
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </template>
        </section>

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>

    </div>

</template>
<script>

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');

    module.exports = {
        props: {
            alumno: {}
        },

        components: {
            ModalConfirm, ModalInfo
        },

        data() {
            return {
                evaluado: {},
                temas: [],
                notasNivelacion: [],
                historial: []
            };
        },

        mounted() {

        },

        methods: {
            obtenerDatos() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/notasAdmision`,
                    body: {id: this.alumno.id}
                })).then((resp) => {
                    this.evaluado = resp.data.data.evaluado;
                    this.notasNivelacion = resp.data.data.notasNivelacion;
                    this.historial = resp.data.data.historial;

                    let temasAll = resp.data.data.temasExamen;
                    temasAll.forEach(tex => {
                        tex.nota = this.getNota(tex.temaExamen.codigo);
                        tex.puntaje = this.getPuntaje(tex.temaExamen.codigo);

                        tex.cursoNiv = {};
                        tex.cicloNiv = {};
                        tex.notaNiv = {};
                        let nota = this.notasNivelacion.find(nan => nan.temaExamen.codigo === tex.temaExamen.codigo);
                        if (nota) {
                            tex.notaNiv = nota;
                            tex.cursoNiv = nota.curso;
                            tex.cicloNiv = nota.alumnoNivelacion.cicloAcademico;
                        }

                        tex.cursoHisto = {};
                        tex.cicloHisto = {};
                        tex.notaHisto = {};
                        let histo = this.historial.find(his => his.temaExamen.codigo === tex.temaExamen.codigo);
                        if (histo) {
                            tex.notaHisto = histo;
                            tex.cursoHisto = histo.curso;
                            tex.cicloHisto = histo.ciclo;
                        }
                    });

                    this.temas = temasAll;
                });
            },

            classNota(nota) {
                if (!nota) {
                    return "";
                }
                if (nota >= 10.5) {
                    return "text-primary bold";
                }
                if (nota < 10.5) {
                    return "text-danger bold";
                }
                return "";
            },
            classColorNota(nota) {
                if (!nota) {
                    return "";
                }
                if (nota.aprobado) {
                    return "text-primary bold";
                }
                if (!nota.aprobado) {
                    return "text-danger bold";
                }
                return "";
            },

            getNota(code) {
                if (code === 'RV') {
                    return this.evaluado.notaRv;
                }
                if (code === 'RM') {
                    return this.evaluado.notaRm;
                }
                if (code === 'MAT') {
                    return this.evaluado.notaMatematicas;
                }
                if (code === 'ARI') {
                    return this.evaluado.notaAritmetica;
                }
                if (code === 'ALG') {
                    return this.evaluado.notaAlgebra;
                }
                if (code === 'GEOM') {
                    return this.evaluado.notaGeometria;
                }
                if (code === 'TRI') {
                    return this.evaluado.notaTrigonometria;
                }
                if (code === 'FIS') {
                    return this.evaluado.notaFisica;
                }
                if (code === 'QUI') {
                    return this.evaluado.notaQuimica;
                }
                if (code === 'BIO') {
                    return this.evaluado.notaBiologia;
                }
                if (code === 'ECO') {
                    return this.evaluado.notaEconomia;
                }
                if (code === 'HIS') {
                    return this.evaluado.notaHistoria;
                }
                if (code === 'GEOG') {
                    return this.evaluado.notaGeografia;
                }
            },
            getPuntaje(code) {
                if (code === 'RV') {
                    return this.evaluado.puntajeRv;
                }
                if (code === 'RM') {
                    return this.evaluado.puntajeRm;
                }
                if (code === 'MAT') {
                    return this.evaluado.puntajeMatematicas;
                }
                if (code === 'ARI') {
                    return this.evaluado.puntajeAritmetica;
                }
                if (code === 'ALG') {
                    return this.evaluado.puntajeAlgebra;
                }
                if (code === 'GEOM') {
                    return this.evaluado.puntajeGeometria;
                }
                if (code === 'TRI') {
                    return this.evaluado.puntajeTrigonometria;
                }
                if (code === 'FIS') {
                    return this.evaluado.puntajeFisica;
                }
                if (code === 'QUI') {
                    return this.evaluado.puntajeQuimica;
                }
                if (code === 'BIO') {
                    return this.evaluado.puntajeBiologia;
                }
                if (code === 'ECO') {
                    return this.evaluado.puntajeEconomia;
                }
                if (code === 'HIS') {
                    return this.evaluado.puntajeHistoria;
                }
                if (code === 'GEOG') {
                    return this.evaluado.puntajeGeografia;
                }
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas,
            score: myUtils.score
        }
    };
</script>