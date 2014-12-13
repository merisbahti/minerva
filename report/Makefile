xetexit:
	mkdir -p output
	xelatex --output-directory output/ naaclhlt2015.tex

# Observe!
# only for fish users with fswatch installed.
# If someone would create a similar version for bash 
# it would be greatly appreciated.
reloader:
	fish -c "if type fswatch >/dev/null ^/dev/null;\
             while true; fswatch naaclhlt2015.tex | xargs -n1 xelatex --output-directory output; end;\
           else;\
             echo install fswatch;\
           end;"
