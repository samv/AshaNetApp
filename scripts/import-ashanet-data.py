
import collections
import io
import json
import re
from StringIO import StringIO

from lxml import etree
from lxml.etree import dump
from normalize import JsonRecord, Property, JsonProperty
from normalize.property import make_property_type
from normalize.property.types import (
    DateProperty,
    FloatProperty,
    IntProperty,
    StringProperty,
    UnicodeProperty,
)
import requests


IdProperty = make_property_type(
    "IdProperty", (StringProperty, JsonProperty),
    json_name="objectId",
    check=lambda x: re.match(r'^\w{10}$', x),
)


winfix = {
    128: u"\x20ac", 130: u"\x201A", 131: u"\x0192", 132: u"\x201e",
    133: u"\x2026", 134: u"\x2020", 135: u"\x2021", 136: u"\x02C6",
    137: u"\x2030", 138: u"\x0160", 139: u"\x2039", 140: u"\x0152",
    142: u"\x017D", 145: u"\x2018", 146: u"\x2019", 147: u"\x201C",
    148: u"\x201D", 149: u"\x2022", 150: u"\x2013", 151: u"\x2014",
    152: u"\x02DC", 153: u"\x2122", 154: u"\x0161", 155: u"\x203A",
    156: u"\x0153", 158: u"\x017E", 159: u"\x0178", 160: u" ",
}


def fix_cp1252(m):
    return m.group(1) + winfix[ord(m.group(2))] + m.group(3)


def fix_bad_encoding(unistr):
    try:
        return re.sub(r"(^|[ -~])([\x80-\xa0])([ -~]|$)", fix_cp1252, unistr).encode(
            'raw_unicode_escape'
        ).decode('utf-8').replace(u"\ufffd", "")
    except Exception as e:
        try:
            return unistr.encode("iso-8859-1").decode("iso-8859-1")
        except Exception as e2:
            import ipdb; ipdb.set_trace()
            return unistr


TextProperty = make_property_type(
    "TextProperty", UnicodeProperty,
)


class Project(JsonRecord):
    id = IdProperty()
    project_id = IntProperty()
    primary_key = [project_id]
    name = UnicodeProperty()
    description = UnicodeProperty()
    org_description = UnicodeProperty()
    purpose = UnicodeProperty()
    additional_info = UnicodeProperty()
    secondary_focus = StringProperty()
    state = StringProperty()
    project_type = IntProperty()
    focus = IntProperty()
    area = StringProperty()
    address = UnicodeProperty()
    contact_chapter = IdProperty(json_name="contact_chapter")
    first_funded = IntProperty()
    total_funds = FloatProperty()
    status = IntProperty()
    images = Property(isa=list)


class Event(JsonRecord):
    id = IdProperty()
    chapter = IdProperty()
    projects = Property(isa=list)
    name = UnicodeProperty() 
    description = UnicodeProperty() 
    location = UnicodeProperty() 
    event_start = DateProperty()
    event_pricing = Property(isa=dict)
    rsvp_tickets = StringProperty()
    images = Property(isa=list)


class Donations(JsonRecord):
    id = IdProperty()
    donation_amount = FloatProperty()
    project_id = IdProperty()
    chapter_id = IdProperty()
    receipt_info = UnicodeProperty()


class Chapter(JsonRecord):
    id = IdProperty()
    chapter_id = IntProperty()
    name = UnicodeProperty()
    established_year = IntProperty()
    chapter_url = StringProperty()


class ChapterAdmin(JsonRecord):
    admin_user = IdProperty()
    chapter_id = IdProperty()


class Status(JsonRecord):
    id = IdProperty()
    status_id = IntProperty()
    title = UnicodeProperty()
    description = UnicodeProperty()


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
            id="status%.4d" % int(cells[0].text),
            status_id=cells[0].text, 
            title=cells[1].text, 
            description=innerhtml(cells[2]),
        )
    )


class ProjectType(JsonRecord):
    id = IdProperty()
    project_type_id = IntProperty()
    title = UnicodeProperty()
    description = UnicodeProperty()


def innerhtml(element):
    string = etree.tostring(element)
    it = re.sub(
        r'\n+', '<br />\n',
        re.sub(
            r'[ \n\r]*(</?p>|<br/?>|</?td>|<a [^>]*>|\n+\t|\r|&#13;?)+', '\n',
            fix_bad_encoding(
                re.sub("&#(\d+);?", lambda m: unichr(int(m.group(1))), string),
            )
        ).strip()
    )
    return it


def get_project_type():
    return get_table(
        "http://www.ashanet.org/projects/project_type.php",
        lambda cells: ProjectType(
            id="projtype%.2d" % int(cells[0].text),
            project_type_id=cells[0].text, 
            title=cells[1].text, 
            description=innerhtml(cells[2]),
        )
    )


class FocusType(JsonRecord):
    id = IdProperty()
    focus_type_id = IntProperty()
    title = UnicodeProperty()
    description = UnicodeProperty()


def get_focus_type():
    return get_table(
        "http://www.ashanet.org/projects/project_type.php",
        lambda cells: FocusType(
            id="focustyp%.2d" % int(cells[0].text),
            focus_type_id=cells[0].text, 
            title=cells[1].text, 
            description=innerhtml(cells[2]),
        )
    )


def get_project_list():

    def make_project(cells):
        project_id = int(
            cells[1].find('a[@href]').attrib['href'].split("=")[1]
        )
        return Project(
            id="proj%.6d" % project_id,
            name=cells[1].find('a[@href]').text,
            project_id=project_id,
            state=cells[3].text,
        )
    
    return get_table(
        "http://www.ashanet.org/projects/project.php", make_project,
    )


class State(JsonRecord):
    id = IdProperty()
    state_id = IntProperty()
    name = UnicodeProperty()
    population_count = UnicodeProperty()
    projects_count = IntProperty(extraneous=True)
    literacy_percent = FloatProperty()
    sent_amount = FloatProperty()
    capital = UnicodeProperty()


def get_state():

    def make_state(cells):
        state_id = cells[1].find('a[@href]').attrib['href'].split("=")[1]
        kwargs = {}
        if cells[5].text and cells[5].text.replace("&nbsp", ""):
            kwargs['projects_count'] = cells[5].text
        if cells[6].text:
            kwargs['sent_amount'] = cells[6].text.strip("$").replace(",", "")
        if cells[2].text and cells[2].text.replace("&nbsp", ""):
            kwargs['capital'] = cells[2].text
        return State(
            id="IndianST%.2d" % int(state_id),
            name=" ".join(x.capitalize() for x in
                          cells[1].find('a[@href]').text.split(" ")),
            state_id=state_id,
            population_count=long(float(cells[3].text.strip()) * 1e6),
            literacy_percent=cells[4].text,
            **kwargs)
    
    return get_table("http://www.ashanet.org/projects/state.php", make_state)


def load_table(table, type_):
    return list(type_(x) for x in
                json.load(io.open(table + ".json"))['results'])


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
        #if not saffron_th:
            #import ipdb; ipdb.set_trace()
        saffron_data = dict()
        for x in saffron_th:
            next_row = x.getparent().getnext()
            key = x.text.strip()
            if next_row is not None:
                td = next_row.find("td")
                if td.text:
                    saffron_data[key] = "\n".join(
                        fix_bad_encoding(x.strip()) for x in td.itertext() if "Tel:" not in x
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

        body_data = dict()
        body_string = etree.tostring(main_cell)
        #
        #if project.project_id == 1105:
        #    import ipdb; ipdb.set_trace()
        for block in re.split(
            r'<(?:span|p) class="?heading2?"?>', body_string,
        ):
            split_block = re.split(r'</(?:span|p)>', block, 1)
            if len(split_block) > 1:
                heading, body = tuple(x.strip() for x in split_block)
                body_data[heading.strip(":")] = re.sub(
                    r'\n+', '<br />\n',
                    re.sub(
                        r'[ \n\r]*(</?p>|<br/?>|</td>|\n+\t|\r|&#13;?)+', '\n',
                        fix_bad_encoding(
                            re.sub("&#(\d+);?", lambda m: unichr(int(m.group(1))), body),
                        )
                    ).strip()
                )
            else:
                if "Sorry, this is" in split_block[0] and not hasattr(
                        project, 'status'):
                   project.status = 7
                else:
                    stripped = re.sub(r'[ \n]*(</?p>|<br/?>|</?td>|\n+\t)+', '\n', split_block[0]).strip().replace("\n", "<br />\n")
                    if stripped: 
                        pass
                        #print "<!-- start -->", stripped, "<!-- end -->"
        
        if 'Project Type' in body_data:
           m = re.search(r'.php#(\d+)"', body_data['Project Type'])
           if m:
               project.project_type = m.group(1)
        if 'Primary Focus' in body_data:
           m = re.search(r'.php#(\d+)"', body_data['Primary Focus'])
           if m:
               project.focus = m.group(1)
        if 'Secondary Focus' in body_data:
            project.secondary_focus = body_data['Secondary Focus']
        if 'Project Description' in body_data:
            project.description = body_data['Project Description']
            #if "<center>" in project.description:
                #import ipdb; ipdb.set_trace()
        if 'Purpose / Goals' in body_data:
            project.purpose = body_data['Purpose / Goals']
        if 'Organization Description' in body_data:
            project.org_description = body_data['Organization Description']

        if 'Additional Information' in body_data:
            project.additional_info = body_data['Additional Information']

        if not hasattr(project, 'status'):
            project.status = 6
        elif not body_data and project.status != 7:
            import ipdb; ipdb.set_trace()


        #print repr(project)

    return (
        x for x in project_list if (
            getattr(x, "status", 6) in (2, 4) and hasattr(x, "description")
            #and "<center>" not in x.description
        )
    )


if __name__ == "__main__":
    import sys
    if len(sys.argv) < 2:
        raise Exception("Usage: import-ashanet-data.py [table]")
    func = globals()["get_" + sys.argv[1].lower()]
    data = func()
    print json.dumps({"results": [x.json_data() for x in data]}, indent=2)
