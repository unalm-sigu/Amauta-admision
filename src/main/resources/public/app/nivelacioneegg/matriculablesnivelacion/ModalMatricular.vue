<template>
    <modal-vik ref="modalMatricular"
               v-bind="modalMatricular"
               v-bind:okaction="saveinscripcion">
        <div slot="body">

            <h4 class="text-primary block m-b-md m-t-xs">{{title}} {{ciclo.descripcion}}</h4>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="form-group">
                    </div>

                    <template v-if="notaAlumno">
                        <div class="block h5 m-b-xs">
                            <strong>Alumno:</strong>
                            {{notaAlumno.alumnoNivelacion.alumno.persona.apellidosNombres}}
                        </div>
                        <div class="block h5 m-t-xs m-b-xs">
                            <strong>Matrícula:</strong>
                            {{notaAlumno.alumnoNivelacion.alumno.codigo}}
                            &nbsp; &nbsp;
                            <strong>{{notaAlumno.alumnoNivelacion.alumno.persona.tipoDocumento.simbolo}}:</strong>
                            {{notaAlumno.alumnoNivelacion.alumno.persona.numeroDocIdentidad}}
                        </div>
                        <div class="block h5 m-t-xs m-b-lg">
                            <strong>Tema examen:</strong>
                            {{notaAlumno.temaExamen.nombre}}
                            &nbsp; &nbsp;
                            <strong>Curso:</strong>
                            {{notaAlumno.curso.codigo}} -
                            {{notaAlumno.curso.nombre}}
                        </div>

                        <template>
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Grupo horario</label>
                                        <template>
                                            <multiselect v-model="notaAlumno.grupoHoras"
                                                         v-bind:options="gruposHoras"
                                                         v-bind:allow-empty="false"
                                                         v-on:input="verificarCruce"
                                                         track-by="id"
                                                         placeholder="Seleccione un grupo"
                                                         v-bind:showNoOptions="true"
                                                         v-bind:show-labels="false">

                                                <template slot="singleLabel" slot-scope="props">
                                                    <span class="text-primary h4">{{ props.option.codigo }}</span>
                                                </template>

                                                <template slot="option" slot-scope="props">
                                                    <span class="block bold">{{ props.option.codigo }} </span>
                                                </template>

                                                <template slot="noOptions">Lista vacía</template>
                                                <template slot="noResult">Sin resultados</template>

                                            </multiselect>
                                            <input v-bind:value="getObjectId(notaAlumno.grupoHoras)" required="true" type="text" class="hide"/>
                                        </template>
                                    </div>
                                </div>

                                <div class="col-md-8">
                                    <div v-if="loadingSecciones"
                                         class="alert alert-primary">
                                        <h4>Cargando datos...</h4>
                                    </div>
                                    
                                    <div v-else-if="hayCruce"
                                         class="alert alert-danger">
                                        <h4>{{detalleCruce}}</h4>
                                    </div>

                                    <div v-else="" 
                                         class="form-group">
                                        <label>Sección:</label>
                                        <multiselect v-model="notaAlumno.cursoNivelacion"
                                                     v-bind:options="secciones"
                                                     v-bind:allow-empty="false"
                                                     v-on:input="selectSeccion"
                                                     track-by="id"
                                                     placeholder="Seleccione una sección"
                                                     v-bind:showNoOptions="true"
                                                     v-bind:show-labels="false">

                                            <template slot="singleLabel" slot-scope="props">
                                                <span class="text-primary h4">{{ props.option.codigo }}</span>
                                            </template>

                                            <template slot="option" slot-scope="props">
                                                <span class="block">
                                                    <strong>Sección: </strong> {{ props.option.codigo }} | 
                                                    <strong>Disponibles: </strong> {{ props.option.disponibles }} | 
                                                    <strong>Aula: </strong> <span v-if='props.option.aula'>{{ props.option.aula.codigo }}</span>
                                                </span>
                                                <span class="block">
                                                    <strong>Docente: </strong> {{ props.option.docente.codigo }}
                                                    <span v-if="props.option.docente.codigo != 'N.N.' ">
                                                        {{ props.option.docente.persona.apellidosNombres }}
                                                    </span>
                                                </span>
                                            </template>

                                            <template slot="noOptions">Lista vacía</template>
                                            <template slot="noResult">Sin resultados</template>

                                        </multiselect>
                                        <input v-bind:value="getObjectId(notaAlumno.grupoHoras)" required="true" type="text" class="hide"/>
                                    </div>
                                </div>
                            </div>
                        </template>

                        <template v-if="notaAlumno.cursoNivelacion">
                            <div class="block h5 m-t-xs m-b-xs">
                                <strong>Aula:</strong>
                                <template v-if="notaAlumno.cursoNivelacion.aula">
                                    {{notaAlumno.cursoNivelacion.aula.codigo}}
                                </template>
                                &nbsp; &nbsp;
                                <strong>Docente:</strong> {{ notaAlumno.cursoNivelacion.docente.codigo }}
                                <span v-if="notaAlumno.cursoNivelacion.docente.codigo != 'N.N.' ">
                                    - {{ notaAlumno.cursoNivelacion.docente.persona.apellidosNombres }}
                                </span>
                            </div>
                            <div class="block h5 m-t-xs m-b-lg">
                                <strong>Vacantes:</strong>
                                {{notaAlumno.cursoNivelacion.vacantes}}
                                &nbsp; &nbsp;
                                <strong>Matriculados:</strong>
                                {{notaAlumno.cursoNivelacion.matriculados}}
                                &nbsp; &nbsp;
                                <strong>Disponibles:</strong>
                                <span v-bind:class="classVacantes(notaAlumno.cursoNivelacion.disponibles)">
                                    {{notaAlumno.cursoNivelacion.disponibles}}
                                </span>
                            </div>
                            <div v-if="notaAlumno.cursoNivelacion.disponibles == 0"
                                 class="alert alert-danger">
                                <h4>No hay vacantes disponibles</h4>
                            </div>
                        </template>
                    </template>
                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                notaAlumno: null,
                raptor: null,
                visible: false,
                loadingSecciones: false,
                hayCruce: false,
                detalleCruce: '',
                secciones: [],
                ciclo: JSON.parse(cicloJson),
                gruposHoras: JSON.parse(gruposHorasJson),
                form: "id-form-matricular-alumno",
                title: "Inscribir alumno en una sección ",
                modalMatricular: VUE_MODAL.structFormAjax({
                    id: "id-modal-matricular-alumno",
                    okbtn: "Inscribir en sección",
                    okclass: "btn-primary",
                    showaccept: false
                })
            };
        },
        methods: {
            open(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.hayCruce = false;
                this.secciones = [];
                this.raptor = raptor;
                this.infoAlumno(item);

            },
            infoAlumno(item) {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/infoAlumno`,
                    body: {id: item.id}
                })).then((resp) => {
                    let notaAlumno = resp.data.data;
                    notaAlumno.grupoHorasConfig = null;
                    notaAlumno.grupoHoras = null;
                    if (notaAlumno.cursoNivelacion.grupoHoras) {
                        notaAlumno.grupoHorasConfig = JSON.parse(JSON.stringify(notaAlumno.cursoNivelacion.grupoHoras));
                        notaAlumno.grupoHoras = JSON.parse(JSON.stringify(notaAlumno.cursoNivelacion.grupoHoras));
                    }
                    notaAlumno.cursoNivelacion = null;

                    this.notaAlumno = notaAlumno;
                    this.$refs.modalMatricular.open();
                    this.visible = true;

                    if (this.notaAlumno.grupoHoras) {
                        this.selectGrupo(this.notaAlumno.grupoHoras);
                    }
                });
            },
            verificarCruce(item) {
                this.notaAlumno.cursoNivelacion = null;
                this.secciones = [];
                this.hayCruce = false;
                this.loadingSecciones = true;

                let payload = {
                    grupoHoras: item,
                    cursoCiclo: {curso: {id: this.notaAlumno.curso.id}},
                    alumnoNivelacion: {id: this.notaAlumno.alumnoNivelacion.id}
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/verificarCruce`,
                    body: payload,
                    rejectError: false
                })).then((resp) => {
                    this.loadingSecciones = false;
                    if (resp.data.success) {
                        this.selectGrupo(item);
                    } else {
                        this.hayCruce = true;
                        this.detalleCruce = resp.data.message;
                    }
                });
            },
            selectGrupo(item) {
                this.notaAlumno.cursoNivelacion = null;
                this.secciones = [];

                let payload = {
                    grupoHoras: item,
                    cursoCiclo: {curso: {id: this.notaAlumno.curso.id}}
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allSecciones`,
                    body: payload
                })).then((resp) => this.secciones = resp.data.data);
            },
            selectSeccion(item) {
                this.modalMatricular.showaccept = false;
                if (item.disponibles > 0) {
                    this.modalMatricular.showaccept = true;
                }
            },
            saveinscripcion() {
                const payload = {
                    id: this.notaAlumno.id,
                    cursoNivelacion: {id: this.notaAlumno.cursoNivelacion.id}
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/matricularCurso`,
                    modal: this.$refs.modalMatricular,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            classVacantes(vac) {
                if (vac === 0) {
                    return "text-danger";
                }
                return "text-primary";
            },

            getModal() {
                return this.$refs.modalMatricular;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>