
import json
import re
from StringIO import StringIO

from lxml import etree
from lxml.etree import dump
from normalize import JsonRecord, Property
from normalize.property import make_property_type
from normalize.property.types import (
    DateProperty,
    FloatProperty,
    IntProperty,
    StringProperty,
)
import requests


IdProperty = make_property_type(
    "IdProperty", StringProperty,
    check=lambda x: re.match(r'^\w{10}$', x),
)


class Project(JsonRecord):
    id = IdProperty(json_name="objectId")
    project_id = IntProperty()
    primary_key = [project_id]
    name = StringProperty()
    description = StringProperty()
    purpose = StringProperty()
    secondary_focus = StringProperty()
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
    id = IdProperty(json_name="objectId")
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
    id = IdProperty(json_name="objectId")
    donation_amount = FloatProperty()
    project_id = IdProperty()
    chapter_id = IdProperty()
    receipt_info = StringProperty()


class Chapter(JsonRecord):
    id = IdProperty(json_name="objectId")
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

def load_etree(fn):
    parser = etree.HTMLParser()
    return etree.parse(open(fn), parser)

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
            id="chapter%.3d" % int(cells[0].text),
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


def load_table(table, type_):
    return list(type_(x) for x in
                json.load(open(table + ".json"))['results'])


def get_project():
    project_list = load_table("project_list", Project)
    status_list = load_table("status", Status)
    chapter_list = load_table("chapter", Chapter)
    for project in project_list:
        try:
            doc = load_etree("proj-%d.html" % project.project_id)
        except IOError:
            continue
        #print "Loaded HTML for %r" % project
        project.name = project.name.strip()
        
        # 1. locate all the "saffron headings"
        saffron_th = doc.findall("//th[@class='saffron-heading']")
        saffron_data = dict()
        for x in saffron_th:
            next_row = x.getparent().getnext()
            key = x.text.strip()
            if next_row is not None:
                td = next_row.find("td")
                if td.text:
                    saffron_data[key] = "\n".join(
                        x.strip() for x in td.itertext() if "Tel:" not in x
                    )
                    #if td.getchildren():
                    #saffron_data[key] = td.text.strip()
                elif key == "Funds Disbursed":
                    saffron_data[key] = td.find('p[@class="heading"]').text
                elif key == "Also Stewarding Chapters":
                    links = td.findall("*//a[@class='plain']")
                    if links:
                        saffron_data[key] = "\n".join(x.attrib['href'] for x in links)
                    else:
                        sorry = td.find('p[@class="debug"]')
                        if sorry is not None:
                            saffron_data[key] = None
                        else:
                            pass
                elif key == "Chapter Contact":
                    link = td.find('a[@class="small"]')
                    if link is not None:
                        saffron_data[key] = link.attrib['href']
                    else:
                        import ipdb; ipdb.set_trace()
                else:
                    import ipdb; ipdb.set_trace()
                    1
            else:
                import ipdb; ipdb.set_trace()
                #print dump(x)

        if 'Status' in saffron_data:
            for status in status_list:
                if saffron_data['Status'].startswith(status.title):
                    project.status = status.status_id
            if not hasattr(project, "status"):
                import ipdb; ipdb.set_trace();
                1
        if 'Project Address' in saffron_data:
            project.address = saffron_data['Project Address']
        if 'Funds Disbursed' in saffron_data:
            project.total_funds = (
                saffron_data['Funds Disbursed'].lstrip("Total = $")
                .replace(",", "")
            )
        if 'Chapter Contact' in saffron_data:
            for chapter in chapter_list:
                if chapter.chapter_url == saffron_data['Chapter Contact']:
                    if not hasattr(chapter, "id"):
                        import ipdb; ipdb.set_trace()
                    project.contact_chapter = chapter.id

        # 2. locate the description cell
        main_cell = doc.find("//table[@width='860'][2]/tr/td[2]/table/tr/td")
        text = list(y for y in (x.strip() for x in main_cell.itertext()) if y)
        for i in range(0, len(text)):
            ti = text[i].strip()
            if ti == "Project Description":
                project.description = text[i + 1].strip()
            elif ti == "Area:":
                project.area = text[i + 1].strip()
            elif ti == "Secondary Focus:":
                project.secondary_focus = text[i + 1].strip()
        desc = list(x for x in main_cell.iterdescendants())
        for x in desc:
            if x.tag == "a" and 'href' in x.attrib:
                link = x.attrib['href']
                m = re.match(r'(project_type|focus).php#(\d+)$', link)
                if m:
                    setattr(project, m.group(1), m.group(2))
                else:
                    pass

    return project_list


if __name__ == "__main__":
    import sys
    if len(sys.argv) < 2:
        raise Exception("Usage: import-ashanet-data.py [table]")
    func = globals()["get_" + sys.argv[1].lower()]
    data = func()
    print json.dumps({"results": [x.json_data() for x in data]}, indent=2)
