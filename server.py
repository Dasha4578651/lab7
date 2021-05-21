import bottle

start_page = """
Start page
<br></br>
<h2><a href="http://localhost/page1">1</a></h2>
"""
page = """
Page number {{num}}
<h2><a href="http://localhost/dead{{int(num)}}">Dead route</a></h2>
<h2><a href="http://localhost/page{{int(num) + 1}}">{{int(num) + 1}}</a></h2>
<h2><a href="http://localhost/page{{int(num)}}">{{int(num)}}</a></h2>
"""

# start_page = """Start page
# <a href="http://localhost/page">Link</a>
# """
# page = """Page page
# <a href="http://localhost/">Home</a>
# """

@bottle.route('/')
def default():
    return bottle.template(start_page)
@bottle.route("/page<number:re:[0-9]*>")
def page_number(number):
    return bottle.template(page, num=number)
@bottle.route("/dead<number:re:[0-9]*>")
def dead_route(number):
    return "<h1>Dead route!</h1>"
# @bottle.route('/page')
# def page_handle():
#     return page

bottle.run(host='localhost', port=80)