<template>
    <modal-vik ref="modalAddCurso"
               v-bind="modalAddCurso"
               v-bind:okaction="saveAddACurso">
        <div slot="body">

            <h3 class="text-primary block bold m-b m-t">{{title}} {{ciclo.descripcion}}</h3>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="row">
                        <div class="col-md-10">
                            <div class="form-group">
                                <label>Curso</label>

                                <multiselect v-model="cursoNiv.cursoCiclo.curso"
                                             v-bind:options="cursos"
                                             v-bind:allow-empty="false"
                                             v-on:search-change="searchCursoDebounce"
                                             v-on:input="selectCurso"
                                             track-by="id"
                                             placeholder="Seleccione un curso"
                                             v-bind:internal-search="false"
                                             v-bind:showNoOptions="true"
                                             v-bind:show-labels="false">

                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="text-primary h4">{{ props.option.nombre }}</span>
                                    </template>

                                    <template slot="option" slot-scope="props">
                                        <span class="block">{{ props.option.codigo }} - {{ props.option.nombre }}</span>
                                    </template>

                                    <template slot="noOptions">Lista vacía</template>
                                    <template slot="noResult">Sin resultados</template>

                                </multiselect>

                                <input v-bind:value="getObjectId(cursoNiv.cursoCiclo.curso)" required="true" type="text" class="hide"/>

                            </div>
                        </div>
                        <div class="col-md-2">
                            <template v-if="cursoNiv.cursoCiclo && cursoNiv.cursoCiclo.curso">
                                <label>Código</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    {{cursoNiv.cursoCiclo.curso.codigo}}
                                </span>
                            </template>
                        </div>
                    </div>

                    <template v-if="cursoNiv.cursoCiclo.curso">

                        <div class="row">
                            <div class="col-md-4">
                                <label>Horas dictado</label>
                                <input v-if="horasEditables" v-model="cursoNiv.horasDictado" type="text" class="form-control numeric" required="yes" maxlength="3"/>
                                <span v-else="" class="item-form-control item-form-gray text-primary">
                                    {{cursoNiv.horasDictado}}
                                </span>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Grupo horario</label>
                                    <multiselect v-model="cursoNiv.grupoHoras"
                                                 v-bind:options="gruposHoras"
                                                 v-bind:allow-empty="false"
                                                 v-on:input="selectGrupo"
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
                                    <input v-bind:value="getObjectId(cursoNiv.grupoHoras)" required="true" type="text" class="hide"/>
                                </div>
                            </div>

                            <div class="col-md-4"
                                 v-if="cursoNiv.grupoHoras">
                                <label>Horario configurado</label>
                                <template v-if="horarios.length == 0">
                                    <span class="item-form-control item-form-gray text-danger">
                                        Falta configurar
                                    </span>
                                </template>
                                <template v-else="">
                                    <span class="item-form-control item-form-gray text-primary">
                                        {{horarios.length}} horas
                                    </span>
                                </template>
                            </div>

                        </div>

                        <div class="row"
                             v-if="cursoNiv.grupoHoras">
                            <div class="col-md-4">
                                <label>Fecha inicio</label>
                                <template v-if="periodoEditable">
                                    <div class="input-group">
                                        <date-picker
                                            required="yes"
                                            v-bind:config="configDate" 
                                            v-bind:wrap="false"
                                            v-on:dp-change="selectFechaInicio"
                                            v-model="cursoNiv.fechaReferencia"></date-picker>
                                        <div class="input-group-addon">
                                            <span class="fa fa-calendar"></span>
                                        </div>
                                    </div>
                                </template>
                                <template v-else="">
                                    <span class="item-form-control item-form-gray text-primary">
                                        {{cursoNiv.fechaReferencia}}
                                    </span>
                                </template>
                            </div>

                            <div class="col-md-4">
                                <label>Periodo</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    <span v-if="cursoNiv.fechaInicio">
                                        {{cursoNiv.fechaInicio}} al {{cursoNiv.fechaFin}}
                                    </span>
                                </span>
                            </div>

                            <div class="col-md-4">
                                <label>Vacantes</label>
                                <input v-model="cursoNiv.vacantes" type="text" class="form-control numeric" required="yes" maxlength="3"/>
                            </div>
                        </div>
                    </template>

                    <template v-if="cursoNiv.grupoHoras">
                        <div class="row m-t-sm">
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Aula</label>
                                    <multiselect v-model="cursoNiv.aula"
                                                 v-bind:options="aulas"
                                                 v-bind:allow-empty="false"
                                                 v-on:search-change="searchAulaDebounce"
                                                 v-on:input="selectAula"
                                                 track-by="id"
                                                 placeholder="Seleccione un aula"
                                                 v-bind:internal-search="false"
                                                 v-bind:showNoOptions="true"
                                                 v-bind:show-labels="false">

                                        <template slot="singleLabel" slot-scope="props">
                                            <span class="text-primary h4">{{ props.option.codigo }}</span>
                                        </template>

                                        <template slot="option" slot-scope="props">
                                            <span class="block">
                                                <span class="bold"> {{ props.option.codigo }} </span>
                                                <span v-if="props.option.nombre" class=""> - {{ props.option.nombre }} </span>
                                            </span>
                                            <span class="block">
                                                Cap: {{ props.option.capacidadAula }} - 
                                                Edif: {{ props.option.aulaSuperior.nombre}}
                                            </span>
                                        </template>

                                        <template slot="noOptions">Lista vacía</template>
                                        <template slot="noResult">Sin resultados</template>

                                    </multiselect>
                                    <input v-bind:value="getObjectId(cursoNiv.aula)" type="text" class="hide"/>
                                </div>
                            </div>

                            <template v-if="cursoNiv.aula">
                                <div class="col-md-4">
                                    <label>Edificio / Pabellón</label>
                                    <span class="item-form-control item-form-gray text-primary">
                                        {{ cursoNiv.aula.aulaSuperior.nombre }}
                                    </span>
                                </div>

                                <div class="col-md-4">
                                    <label>Capacidad / Aforo</label>
                                    <span class="item-form-control item-form-gray text-primary">
                                        {{ cursoNiv.aula.capacidadAula }} /
                                        {{ cursoNiv.aula.aforo }}
                                    </span>
                                </div>
                            </template>
                        </div>
                    </template>

                    <div v-if="hayCruceAula" class="alert alert-danger">
                        <h4>{{mensajeCruceAula}}</h4>
                    </div>

                    <template v-if="cursoNiv.grupoHoras">
                        <div class="row">
                            <div class="col-md-10">
                                <div class="form-group">
                                    <label>Docente</label>
                                    <multiselect v-model="cursoNiv.docente"
                                                 v-bind:options="docentes"
                                                 v-bind:allow-empty="false"
                                                 v-on:search-change="searchDocenteDebounce"
                                                 v-on:input="selectDocente"
                                                 track-by="id"
                                                 placeholder="Seleccione un docente"
                                                 v-bind:internal-search="false"
                                                 v-bind:showNoOptions="true"
                                                 v-bind:show-labels="false">

                                        <template slot="singleLabel" slot-scope="props">
                                            <span class="text-primary h4">
                                                <template v-if="props.option.persona">
                                                    {{ props.option.persona.apellidosNombres }}
                                                </template>
                                                <template v-else="">
                                                    Desconocido
                                                </template>
                                            </span>
                                        </template>

                                        <template slot="option" slot-scope="props">
                                            <span class="block">
                                                <span class="bold text-primary"> 
                                                    {{ props.option.codigo }} - 
                                                    <template v-if="props.option.persona">
                                                        {{ props.option.persona.apellidosNombres }}
                                                    </template>
                                                    <template v-else="">
                                                        Desconocido
                                                    </template>
                                                </span>
                                            </span>
                                            <span v-if="props.option.departamentoAcademico" class="block">
                                                Dpto: {{ props.option.departamentoAcademico.nombre }} - 
                                                Fac: {{ props.option.departamentoAcademico.facultad.nombre }}
                                            </span>
                                        </template>

                                        <template slot="noOptions">Lista vacía</template>
                                        <template slot="noResult">Sin resultados</template>

                                    </multiselect>
                                    <input v-bind:value="getObjectId(cursoNiv.docente)" required="true" type="text" class="hide"/>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <template v-if="cursoNiv.docente">
                                    <label>Código</label>
                                    <span class="item-form-control item-form-gray text-primary">
                                        {{cursoNiv.docente.codigo}}
                                    </span>
                                </template>
                            </div>
                        </div>
                    </template>

                    <div v-if="hayCruceDocente" class="alert alert-danger">
                        <h4>{{mensajeCruceDocente}}</h4>
                    </div>
                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                visible: false,
                hayCruceAula: false,
                hayCruceDocente: false,
                periodoEditable: false,
                horasEditables: false,
                mensajeCruceDocente: null,
                mensajeCruceAula: null,
                cursoNiv: null,
                raptor: null,
                aulas: [],
                cursos: [],
                horarios: [],
                docentes: [],
                gruposHoras: JSON.parse(gruposHorasJson),
                ciclo: JSON.parse(cicloJson),
                form: "id-form-add-curso",
                title: "Agregar curso de nivelación",
                modalAddCurso: VUE_MODAL.structFormAjax({
                    id: "id-modal-add-curso",
                    okbtn: "Guardar datos",
                    okclass: "btn-primary",
                    modalsize: "modal-lg"
                }),
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                },
                plantilla: {
                    cursoCiclo: {curso: null},
                    grupoHoras: null,
                    aula: null,
                    docente: null,
                    horasDictado: null,
                    vacantes: null,
                    fechaReferencia: null,
                    fechaInicio: null,
                    fechaFin: null
                }
            };
        },

        created() {
            this.searchCursoDebounce = debounce(this.searchCurso, 800);
            this.searchAulaDebounce = debounce(this.searchAula, 800);
            this.searchDocenteDebounce = debounce(this.searchDocente, 800);
        },

        methods: {
            open(raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.aulas = [];
                this.cursos = [];
                this.horarios = [];
                this.docentes = [];
                this.horasEditables = false;
                this.periodoEditable = true;
                this.hayCruceAula = false;
                this.hayCruceDocente = false;
                this.mensajeCruceAula = null;
                this.mensajeCruceDocente = null;

                this.raptor = raptor;
                this.cursoNiv = JSON.parse(JSON.stringify(this.plantilla));
                this.visible = true;
                this.$refs.modalAddCurso.open();
                myUtils.activarNumeric();
            },

            searchCurso(nombre) {
                if (nombre) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/searchCurso?nombre=${nombre}`
                    })).then((resp) => this.cursos = resp.data.data);
                }
            },
            searchAula(nombre) {
                if (nombre) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/searchAula?nombre=${nombre}`
                    })).then((resp) => this.aulas = resp.data.data);
                }
            },
            searchDocente(nombre) {
                if (nombre) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/searchDocente?nombre=${nombre}`
                    })).then((resp) => this.docentes = resp.data.data);
                }
            },

            selectFechaInicio() {
                if (this.cursoNiv.fechaReferencia) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/getPeriodo`,
                        body: {fechaReferencia: this.cursoNiv.fechaReferencia}
                    })).then((resp) => {
                        let data = resp.data.data;
                        this.cursoNiv.fechaInicio = data.fechaInicio;
                        this.cursoNiv.fechaFin = data.fechaFin;
                    });
                }
            },

            selectCurso(item) {
                this.horasEditables = true;
                this.cursoNiv.horasDictado = item.cursoCicloActivo.horasCiclo;
                if (item.cursoCicloActivo.id) {
                    if (item.cursoCicloActivo.horasCiclo > 0) {
                        this.horasEditables = false;
                    }
                }
                myUtils.activarNumeric();
            },
            selectGrupo(item) {
                let payload = {
                    cursoCiclo: {
                        curso: {id: this.cursoNiv.cursoCiclo.curso.id}
                    },
                    grupoHoras: {id: item.id}
                };

                this.periodoEditable = true;
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/getHorario`,
                    body: payload
                })).then((resp) => {
                    let data = resp.data.data;
                    this.horarios = data.horario;

                    let periodo = data.periodo;
                    if (periodo.fechaReferencia) {
                        this.cursoNiv.fechaReferencia = periodo.fechaReferencia;
                        this.cursoNiv.fechaInicio = periodo.fechaInicio;
                        this.cursoNiv.fechaFin = periodo.fechaFin;
                        this.periodoEditable = false;
                    }
                    myUtils.activarNumeric();
                });
            },
            selectAula(item) {
                this.hayCruceAula = false;
                let payload = {
                    cursoCiclo: {
                        curso: {id: this.cursoNiv.cursoCiclo.curso.id}
                    },
                    aula: {id: item.id},
                    grupoHoras: {id: this.cursoNiv.grupoHoras.id}
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/verificarCruceAula`,
                    body: payload
                })).then((resp) => {
                    let data = resp.data.data;
                    this.hayCruceAula = data.hayCruceAula;
                    this.mensajeCruceAula = data.mensajeCruceAula;
                });
            },
            selectDocente(item) {
                this.hayCruceAula = false;
                let payload = {
                    cursoCiclo: {
                        curso: {id: this.cursoNiv.cursoCiclo.curso.id}
                    },
                    docente: {id: item.id},
                    grupoHoras: {id: this.cursoNiv.grupoHoras.id}
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/verificarCruceDocente`,
                    body: payload
                })).then((resp) => {
                    let data = resp.data.data;
                    this.hayCruceDocente = data.hayCruceDocente;
                    this.mensajeCruceDocente = data.mensajeCruceDocente;
                });
            },

            saveAddACurso() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    notify("Debe completar los campos obligatorios", "error");
                    return;
                }

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/addCurso`,
                    modal: this.$refs.modalAddCurso,
                    raptor: this.raptor,
                    body: this.cursoNiv
                }));
            },

            getModal() {
                return this.$refs.modalAddCurso;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>