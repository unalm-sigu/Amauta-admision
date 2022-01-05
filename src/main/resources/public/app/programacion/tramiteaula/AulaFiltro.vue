<template>
    <div>
        <div class="col-xs-4">
            <multiselect
                v-model="aula"
                label='codigo'
                track-by="id"
                placeholder="Aula"
                :options="aulas"
                :allow-empty="true"
                :loading="isLoading" 
                :internal-search="false"
                :hide-selected="false"
                :showNoOptions="true"
                :show-labels="false"
                @search-change="searchAula"
                @select="dispatchAction"
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
            afterchange: {type: Function, default: () => {}}
        },
        data() {
            return{
                aula: null,
                aulas: [],
                isLoading: false
            }
        },
        mounted() {
        },
        methods: {
            searchAula(nombre) {
                if (!nombre) {
                    return;
                }
                let $vue = this;
                $vue.isLoading = true;
                axios_.get("/tramite/aula/allAulaFiltro", {params: {nombre: nombre}})
                        .then(({data}) => {
                            $vue.aulas = data;
                            $vue.isLoading = false;
                        }, () => {
                            $vue.isLoading = false;
                        });
            },
            dispatchAction(data) {
                this.afterchange(data);
            },
            removeAction() {
                this.afterchange(null);
            }
        }
    };
</script>