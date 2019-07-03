import org.groocss.Config

'test'.groocss(new Config().noExts().compress()) {

    body { fontSize 2.em color black }

    article { padding 2.em }

    'thing'.id { fontSize 200.percent }

    keyframes('test') {
        from { color black } to { color red }
    }

}
