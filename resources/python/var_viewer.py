from IPython.core.magics.namespace import NamespaceMagics as __NsMagics__ # Used to query namespace.

__ns_magics__ = __NsMagics__()
__ns_magics__.shell = get_ipython().kernel.shell

for val in __ns_magics__.who_ls():
    # do not create new variables to keep the list clean
    print(" ".join([type(eval(val)).__name__, val, eval(val).__name__ if type(eval(val)).__name__ == "module" else ""]))