Clojure 1.10 datafy/nav demo
============================

This is a very simple demonstration of clojure 1.10's new Datafiable
and Navigable protocols, written to aid my own understanding, after
reading Sean Corfield's [blog
post](http://corfield.org/blog/2018/12/03/datafy-nav/).

This was tested on clojure-1.10.0RC3.

It implements the Datafiable protocol for the Java class
`java.nio.file.Path`, representing each one as a map containing the
Path's name, size and (if it is a directory) any sub-directories and
files.

The easiest way to try this out is by using
[REBL](http://rebl.cognitect.com/). First, edit `deps.edn` and set the
path to your REBL jar.

    clj -R:rebl --main cognitect.rebl

At the REPL prompt, type

    (use 'nav-demo)
	(make-path "/")
	
In the REBL window, you will see the result of `make-path` in the left
pane and the datafied version on the right. You can select the
`children` key and press the rightward-navigation arrow to open up a
list of the subdirectories and files, and again to select one.

Note that the children are returned as a raw sequence of `Path`
objects, and REBL uses the `Datafiable` protocol to turn each one in
turn to data.  This is a bit ugly. To improve this, I implemented the
`Navigable` protocol on the children object.

Close REBL and start again:

    clj -R:rebl --main cognitect.rebl

At the REPL prompt, type

    (use 'nav-demo2)
	(make-path "/")

Now the children just appear as file names, but the navigation works
the same as before.

The code creates a sequence of `Path` objects as before, then a
matching sequence of filenames. Navigable is a special protocol in
that it can be implemented by metadata on an object. An implementation
of nav is set on the sequence of filenames, which, when called,
returns the corresponding `Path`. In other words, it is defined as
follows: (where `paths` is closed over from the surrounding scope.)

    (defn nav [context index value] (get paths index))
	
When you click on a member of the children sequence, `nav` gets called
like with `context` set to the sequence, `index` is the numerical
index of the item, and `value` is its filename. Only the index is used
to look up the get the corresponding path.

Hopefully this will be useful.

