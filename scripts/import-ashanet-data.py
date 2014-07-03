
import json
from lxml import etree
import requests
from StringIO import StringIO

from normalize import JsonRecord, Property
from normalize.property import make_property_type
from normalize.property.types import (
    DateProperty,
    FloatProperty,
    IntProperty,
    StringProperty,
)


IdProperty = make_property_type(
    "IdProperty", StringProperty,
    check=lambda x: re.match(r'^\w{10}$', x),
)


class Project(JsonRecord):
    id = IdProperty()
    project_id = IntProperty()
    name = StringProperty()
    description = StringProperty()
    purpose = StringProperty()
    state = StringProperty()
    project_type = IntProperty()
    focus = IntProperty()
    area = StringProperty()
    address = StringProperty()
    contact_chapter = IdProperty()
    first_funded = IntProperty()
    total_funds = FloatProperty()
    status = IntProperty()
    images = Property(isa=list)


class Event(JsonRecord):
    id = IdProperty()
    chapter = IdProperty()
    projects = Property(isa=list)
    name = StringProperty() 
    description = StringProperty() 
    location = StringProperty() 
    event_start = DateProperty()
    event_pricing = Property(isa=dict)
    rsvp_tickets = StringProperty()
    images = Property(isa=list)


class Donations(JsonRecord):
    id = IdProperty()
    donation_amount = FloatProperty()
    project_id = IdProperty()
    chapter_id = IdProperty()
    receipt_info = StringProperty()


class Chapter(JsonRecord):
    id = IdProperty()
    chapter_id = IntProperty()
    name = StringProperty()
    established_year = IntProperty()
    chapter_url = StringProperty()


class ChapterAdmin(JsonRecord):
    admin_user = IdProperty()
    chapter_id = IdProperty()


class Status(JsonRecord):
    status_id = IntProperty()
    title = StringProperty()
    description = StringProperty()


def get_etree(url):
    r = requests.get(url)
    parser = etree.HTMLParser()
    return etree.parse(StringIO(r.text), parser)

def get_table(url, xform):
    tree = get_etree(url)
    table = tree.find("//table[@border='1']")
    data = list()
    for tr in table.getchildren():
        cells = tr.getchildren();
        if len(cells) and cells[0].tag != "th":
            data.append(xform(cells))

    return data

def get_chapter():
    return get_table(
        "http://www.ashanet.org/projects/chapter.php",
        lambda cells: Chapter(
            chapter_id=cells[0].text, 
            name=cells[1].find('a[@href]').text,
            established_year=cells[2].text,
            chapter_url=cells[3].find('a[@href]').text,
        ),
    )


def get_status():
    return get_table(
        "http://www.ashanet.org/projects/status.php",
        lambda cells: Status(
            status_id=cells[0].text, 
            title=cells[1].text, 
            description=cells[2].text,
        )
    )


class ProjectType(JsonRecord):
    project_type_id = IntProperty()
    title = StringProperty()
    description = StringProperty()


def get_project_type():
    return get_table(
        "http://www.ashanet.org/projects/project_type.php",
        lambda cells: ProjectType(
            project_type_id=cells[0].text, 
            title=cells[1].text, 
            description=cells[2].text,
        )
    )


class FocusType(JsonRecord):
    focus_type_id = IntProperty()
    title = StringProperty()
    description = StringProperty()


def get_focus_type():
    return get_table(
        "http://www.ashanet.org/projects/project_type.php",
        lambda cells: FocusType(
            focus_type_id=cells[0].text, 
            title=cells[1].text, 
            description=cells[2].text,
        )
    )


def get_project_list():
    return get_table(
        "http://www.ashanet.org/projects/project.php",
        lambda cells: Project(
            name=cells[1].find('a[@href]').text,
            project_id=cells[1].find('a[@href]').attrib['href'].split("=")[1],
            state=cells[3].text,
        ),
    )


if __name__ == "__main__":
    import sys
    if len(sys.argv) < 2:
        raise Exception("Usage: import-ashanet-data.py [table]")
    func = globals()["get_" + sys.argv[1].lower()]
    data = func()
    print json.dumps({"results": [x.json_data() for x in data]}, indent=2)
