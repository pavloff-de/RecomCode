from IPython.core.magics.namespace import NamespaceMagics # Used to query namespace.

ns = NamespaceMagics()
ns.shell = get_ipython().kernel.shell

for val in ns.who_ls():
    print type(eval(val)).__name__, val