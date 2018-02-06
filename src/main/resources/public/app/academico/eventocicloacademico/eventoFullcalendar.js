Vue.component('fullcalendar', {
    template: '#full-calendar',
    mounted: function() {
        var vue = this;
        vue.createFullcalendar();
    },
    methods: {
        createFullcalendar: function() {
            var vue = this;
            $('#calendar').fullCalendar({
                header: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'month,basicWeek,basicDay'
                },
                navLinks: true,
                editable: true,
                eventLimit: true,
            });
        },
        props: {
            events: {
                default() {
                    return []
                },
            },

            eventSources: {
                default() {
                    return []
                },
            },

            editable: {
                default() {
                    return true
                },
            },

            selectable: {
                default() {
                    return true
                },
            },

            selectHelper: {
                default() {
                    return true
                },
            },

            header: {
                default() {
                    return {
                        left: 'prev,next today',
                        center: 'title',
                        right: 'month,agendaWeek,agendaDay'
                    }
                },
            },

            defaultView: {
                default() {
                    return 'agendaWeek'
                },
            },

            sync: {
                default() {
                    return false
                }
            },

            config: {
                type: Object,
                default() {
                    return {}
                },
            },
        },

        computed: {
            defaultConfig() {
                const cal = $('#calendar'),
                        self = this
                return {
                    header: this.header,
                    defaultView: this.defaultView,
                    editable: this.editable,
                    selectable: this.selectable,
                    selectHelper: this.selectHelper,
                    aspectRatio: 2,
                    timeFormat: 'HH:mm',
                    events: this.events,
                    eventSources: this.eventSources,

                    eventRender(...args) {
                        if (this.sync) {
                            self.events = cal.fullCalendar('clientEvents')
                        }
                        self.$emit('event-render', ...args)
                    },

                    eventDestroy(event) {
                        if (this.sync) {
                            self.events = cal.fullCalendar('clientEvents')
                        }
                    },

                    eventClick(...args) {
                        self.$emit('event-selected', ...args)
                    },

                    eventDrop(...args) {
                        self.$emit('event-drop', ...args)
                    },

                    eventResize(...args) {
                        self.$emit('event-resize', ...args)
                    },

                    dayClick(...args) {
                        self.$emit('day-click', ...args)
                    },
                    select(start, end, jsEvent, view, resource) {
                        self.$emit('event-created', {
                            start,
                            end,
                            allDay: !start.hasTime() && !end.hasTime(),
                            view,
                            resource
                        })
                    }
                }
            },
        },

        mounted() {
            const cal = $('#calendar'),
                    self = this

            this.$on('remove-event', (event) => {
                if (event && event.hasOwnProperty('id')) {
                    $('#calendar').fullCalendar('removeEvents', event.id);
                } else {
                    $('#calendar').fullCalendar('removeEvents', event);
                }
            })

            this.$on('rerender-events', () => {
                $('#calendar').fullCalendar('rerenderEvents')
            })

            this.$on('refetch-events', () => {
                $('#calendar').fullCalendar('refetchEvents')
            })

            this.$on('render-event', (event) => {
                $('#calendar').fullCalendar('renderEvent', event)
            })

            this.$on('reload-events', () => {
                $('#calendar').fullCalendar('removeEvents')
                $('#calendar').fullCalendar('addEventSource', this.events)
            })

            this.$on('rebuild-sources', () => {
                $('#calendar').fullCalendar('removeEventSources')
                this.eventSources.map(event => {
                    $('#calendar').fullCalendar('addEventSource', event)
                })
            })

            cal.fullCalendar(defaultsDeep(this.config, this.defaultConfig))
        },

        methods: {
            fireMethod(...options) {
                return $('#calendar').fullCalendar(...options)
            },
            metodo1(argg) {
                console.log('fkjsdhfkjshdflkjhsdflkjh fuckooofff');
                console.log(argg);
            }
        },

        watch: {
            events: {
                deep: true,
                handler(val) {
                    $('#calendar').fullCalendar('removeEvents')
                    $('#calendar').fullCalendar('addEventSource', this.events)
                },
            },
            eventSources: {
                deep: true,
                handler(val) {
                    this.$emit('rebuild-sources');
                },
            },
        },

        beforeDestroy() {
            this.$off('remove-event');
            this.$off('rerender-events');
            this.$off('refetch-events');
            this.$off('render-event');
            this.$off('reload-events');
            this.$off('rebuild-sources');
        },
    }
});
