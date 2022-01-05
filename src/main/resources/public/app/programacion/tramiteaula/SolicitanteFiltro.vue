<template>
    <div>
        <div class="col-xs-2">
            <multiselect
                v-model="tipo"
                label='nombre'
                track-by="id"
                placeholder="Solicitante"
                @remove="removeAction"
                @select="removeAction"
                :options="tipos"
                :allow-empty="true"
                :internal-search="true"
                :hide-selected="false"
                :showNoOptions="true"
                :show-labels="false">
                <template slot="singleLabel" slot-scope="props">
                    <span class="option__title">
                        {{props.option.nombre}}
                    </span>
                </template>
                <template slot="option" slot-scope="props">
                    <span class="option_title">
                        {{props.option.nombre}}
                    </span> 
                </template>
                <template slot="noOptions">&nbsp</template>
                <template slot="noResult">&nbsp</template>
            </multiselect>
        </div>

        <div v-if="tipo&&tipo.id=='ALU'" class="col-xs-4">
            <multiselect
                v-model="alumno"
                label='codigo'
                track-by="id"
                placeholder="Alumno"
                :options="alumnos"
                :allow-empty="true"
                :loading="isLoading" 
                :internal-search="false"
                :hide-selected="false"
                :showNoOptions="true"
                :show-labels="false"
                @search-change="searchAlumno"
                @select="dispatchActionAlumno"
                @remove="removeAction">
                <template slot="singleLabel" slot-scope="props">
                    <span class="option__title">
                        <b>{{props.option.codigo}}</b> {{props.option.persona.nombreCompleto}}
                    </span>
                </template>
                <template slot="option" slot-scope="props">
                    <span class="option_title">
                        <b>{{props.option.codigo}}</b> {{props.option.persona.nombreCompleto}}
                    </span> 
                </template>
                <template slot="noOptions">&nbsp</template>
                <template slot="noResult">&nbsp</template>
            </multiselect>
        </div>

        <div v-if="tipo&&tipo.id=='DOC'" class="col-xs-4">
            <multiselect
                v-model="docente"
                label='codigo'
                track-by="id"
                placeholder="Docente"
                :options="docentes"
                :allow-empty="true"
                :loading="isLoading" 
                :internal-search="false"
                :hide-selected="false"
                :showNoOptions="true"
                :show-labels="false"
                @search-change="searchDocente"
                @select="dispatchActionDocente"
                @remove="removeAction">
                <template slot="singleLabel" slot-scope="props">
                    <span class="option__title">
                        <b>{{props.option.codigo}}</b> {{props.option.persona.nombreCompleto}}
                    </span>
                </template>
                <template slot="option" slot-scope="props">
                    <span class="option_title">
                        <b>{{props.option.codigo}}</b> {{props.option.persona.nombreCompleto}}
                    </span> 
                </template>
                <template slot="noOptions">&nbsp</template>
                <template slot="noResult">&nbsp</template>
            </multiselect>
        </div>

        <div v-if="tipo&&tipo.id=='EMP'" class="col-xs-4">
            <multiselect
                v-model="empresa"
                label='numeroDocIdentidad'
                track-by="id"
                placeholder="Empresa"
                :options="empresas"
                :allow-empty="true"
                :loading="isLoading" 
                :internal-search="false"
                :hide-selected="false"
                :showNoOptions="true"
                :show-labels="false"
                @search-change="searchEmpresa"
                @select="dispatchActionEmpresa"
                @remove="removeAction">
                <template slot="singleLabel" slot-scope="props">
                    <span class="option__title">
                        <b>{{props.option.numeroDocIdentidad}}</b> {{props.option.razonSocial}}
                    </span>
                </template>
                <template slot="option" slot-scope="props">
                    <span class="option_title">
                        <b>{{props.option.numeroDocIdentidad}}</b> {{props.option.razonSocial}}
                    </span> 
                </template>
                <template slot="noOptions">&nbsp</template>
                <template slot="noResult">&nbsp</template>
            </multiselect>
        </div>

        <div v-if="tipo&&tipo.id=='OFI'" class="col-xs-4">
            <multiselect
                v-model="oficina"
                label='codigo'
                track-by="id"
                placeholder="Oficina"
                :options="oficinas"
                :allow-empty="true"
                :loading="isLoading" 
                :internal-search="false"
                :hide-selected="false"
                :showNoOptions="true"
                :show-labels="false"
                @search-change="searchOficina"
                @select="dispatchActionOficina"
                @remove="removeAction">
                <template slot="singleLabel" slot-scope="props">
                    <span class="option__title">
                        <b>{{props.option.codigo}}</b> {{props.option.nombre}}
                    </span>
                </template>
                <template slot="option" slot-scope="props">
                    <span class="option_title">
                        <b>{{props.option.codigo}}</b> {{props.option.nombre}}
                    </span> 
                </template>
                <template slot="noOptions">&nbsp</template>
                <template slot="noResult">&nbsp</template>
            </multiselect>
        </div>

    </div>
</template>
<script>
    module.exports = {
        props: {
            afterchange: {type: Function, default: () => {
                }}
        },
        data() {
            return{
                isLoading: false,
                tipo: null,
                tipos: [
                    {id: 'EMP', nombre: 'Empresa'},
                    {id: 'ALU', nombre: 'Alumno'},
                    {id: 'DOC', nombre: 'Docente'},
                    {id: 'OFI', nombre: 'Oficina'},
                ],
                alumno: null,
                alumnos: [],
                docente: null,
                docentes: [],
                empresa: null,
                empresas: [],
                oficina: null,
                oficinas: [],
            }
        },
        mounted() {

        },
        methods: {
            searchAlumno(nombre) {
                if (!nombre) {
                    return;
                }
                let $vue = this;
                $vue.isLoading = true;
                axios_.get("/tramite/aula/allAlumno", {params: {nombre: nombre}})
                        .then(({data}) => {
                            $vue.alumnos = data;
                            $vue.isLoading = false;
                        }, () => {
                            $vue.isLoading = false;
                        });
            },
            searchEmpresa(nombre) {
                if (!nombre) {
                    return;
                }
                let $vue = this;
                $vue.isLoading = true;
                axios_.get("/tramite/aula/allEmpresa", {params: {nombre: nombre}})
                        .then(({data}) => {
                            $vue.empresas = data;
                            $vue.isLoading = false;
                        }, () => {
                            $vue.isLoading = false;
                        });
            },
            searchDocente(nombre) {
                if (!nombre) {
                    return;
                }
                let $vue = this;
                $vue.isLoading = true;
                axios_.get("/tramite/aula/allDocente", {params: {nombre: nombre}})
                        .then(({data}) => {
                            $vue.docentes = data;
                            $vue.isLoading = false;
                        }, () => {
                            $vue.isLoading = false;
                        });
            },
            searchOficina(nombre) {
                if (!nombre) {
                    return;
                }
                let $vue = this;
                $vue.isLoading = true;
                axios_.get("/tramite/aula/allOficina", {params: {nombre: nombre}})
                        .then(({data}) => {
                            $vue.oficinas = data;
                            $vue.isLoading = false;
                        }, () => {
                            $vue.isLoading = false;
                        });
            },
            removeAction() {
                this.afterchange(null);
                this.alumno = null;
                this.docente = null;
                this.empresa = null;
                this.oficina = null;
            },
            dispatchActionDocente(docente) {
                this.afterchange(this.tipo,docente);
            },
            dispatchActionAlumno(alumno) {
                this.afterchange(this.tipo,alumno);
            },
            dispatchActionEmpresa(empresa) {
                this.afterchange(this.tipo,empresa);
            },
            dispatchActionOficina(oficina) {
                this.afterchange(this.tipo,oficina);
            },
        }
    };
</script>