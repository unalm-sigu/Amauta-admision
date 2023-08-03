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
                    <div class="col-md-4">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Tema examen</th>
                                    <th>Nota</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="item in temas">
                                    <td>{{item.temaExamen.nombre}}</td>
                                    <td>{{getNota(item.temaExamen.codigo)}}</td>
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
                temas: []
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
                    this.temas = resp.data.data.temasExamen;
                });
            },

            getNota(code) {
                if (code === 'RV') {
                    return this.evaluado.puntajeRv;
                }
                if (code === 'RM') {
                    return this.evaluado.puntajeRm;
                }
                if (code === 'MAT') {
                    return this.evaluado.puntajeMatematicas;
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
            }
        }
    };
</script>