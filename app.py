from flask import Flask,request,jsonify
from flask_cors import CORS

import time
import urllib.request

import base64
import io

from bs4 import BeautifulSoup

import os

import google.generativeai as genai

import PIL.Image as pl

API=""#Gemini-API

genai.configure(api_key=API)

def getmodel():
    return genai.GenerativeModel("gemini-pro")

def getvision():
    return genai.GenerativeModel("gemini-pro-vision")

def queryimage(mdl,prompt,img):
    response=mdl.generate_content([prompt,img])
    response.resolve()
    #return (parse(response.text),response.text)
    return response.text
    
def querytext(mdl,prompt):
    #print(prompt)
    response=mdl.generate_content(prompt)
    return (response.text)  

model=getmodel()

def get_text(url):
 #   try:
    print("opening")
    #options = webdriver.ChromeOptions()
    print('a')
    chrome_options = webdriver.ChromeOptions()
    chrome_options.binary_location = os.environ.get("GOOGLE_CHROME_BIN")
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("--no-sandbox")
    
    service = Service(executable_path=os.environ.get("CHROMEDRIVER_PATH"))
    testdriver = webdriver.Chrome(service=service, options=chrome_options)
    #testdriver = webdriver.Chrome(executable_path=chrome_location, chrome_options=options)
    #op.add_argument('headless')
    
    #testdriver = webdriver.Chrome(options=op)
    print('b')
    testdriver.get(url)
    time.sleep(5)
    print('c')
    
    body = testdriver.find_element(By.TAG_NAME,"Body")
    
    bodyc=BeautifulSoup(body.get_attribute("innerHTML"))

    print("extracting")
    #print(divbs)
    transcript=bodyc.get_text()
    #print(len(transcript))
    testdriver.quit()
    print(">>>Summarising")
    #response=getsummary(transcript)
    response=transcript
 #   except:
  #      response="ERROR!"

    return response

def gettext(url):
    hdr = {'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
    'Referer': 'https://cssspritegenerator.com',
    'Accept-Charset': 'ISO-8859-1,utf-8;q=0.7,*;q=0.3',
    'Accept-Encoding': 'none',
    'Accept-Language': 'en-US,en;q=0.8',
    'Connection': 'keep-alive'}
    req = urllib.request.Request(url=url, headers=hdr) 
    page = urllib.request.urlopen(req).read()
    
    mystr = page.decode("utf8")
    soup = BeautifulSoup(mystr, 'html.parser')

    body = soup.find('body')

    text = body.get_text()

    return text

def getsummary(data):
    window=4000
    i=0
    summary=''
    while i<len(data):
        text=data[i:i+window]
        temp=querytext(model,"Summarise the following : "+text)
        summary += temp
        i= i+window
    return summary
        

app=Flask(__name__)
CORS(app)

@app.route('/')
def hello():
    return "Hello World!347"


@app.route('/api/data', methods=['GET', 'POST'])
def handle_data():
    if request.method == 'GET':
        #print(f">>>>>>{request.args.get('param')}")
        print(f"&&&&&&{request.query_string}")
        dat=(request.query_string).decode('ascii')
        if len(dat)<2:
            dat='Introduce yourself as a helpful assistant.'
        "random"
        if dat.find("SUMM:")==0:
            print("SUmmarying")
            url=dat.split("SUMM:")[-1]
            data=gettext(url)
            print("HERE")
            res=getsummary(data)
        else:
            res=querytext(model,dat)
        data = {'message': ' ' +res}
        return jsonify(data)
        #return f'This is a GET request. Hello World {str(request.query_string)}'

    elif request.method == 'POST':
        data = request.get_json()
        return f'This is a POST request. Received data: {data}'
        
@app.route('/api/image',methods=["POST"])
def handle_image():
    try:
        #print('a')
        payload = request.form.to_dict(flat=False)
        
        im_b64 = payload['image'][0] 

        prompt=payload['prompt'][0]
        #print(prompt)
        im_binary = base64.b64decode(im_b64)
        #print('d')
        buf = io.BytesIO(im_binary)
        #print('e')
        img = pl.open(buf)
        res=queryimage(getvision(),prompt,img)
        return jsonify({'msg': 'success', 'size': [img.width, img.height], 'desc':res})
    except:
        return jsonify({'msg': 'fail', 'desc':"Error Processing image."})


@app.route('/api/search',methods=['GET'])
def handle_search():
    try:
        #from langchain_google_genai import ChatGoogleGenerativeAI
        from langchain.agents import AgentType,initialize_agent,load_tools
        from langchain.llms import GooglePalm
        
        import langchain
        langchain.debug=True
        PALM_API=""#PALM API key    
        llm = GooglePalm(google_api_key=PALM_API,temperature=0.03,maxOutputTokens=4000)#ChatGoogleGenerativeAI(model="gemini-pro", google_api_key=API)
    
        serp_key=""#google search API key ###pip install google-search-results
        os.environ['SERPAPI_API_KEY']=serp_key
        tools=load_tools(["serpapi"],llm=llm)
        agent=initialize_agent(tools,llm,agent=AgentType.ZERO_SHOT_REACT_DESCRIPTION,handle_parsing_errors=True)
        res=agent.run((request.query_string).decode('ascii'))
        print(res)
    except:
        res="Error searching."
    return jsonify({"message":res})


@app.route('/api/image2',methods=["POST"])
def handle_image2():
    try:
        file = request.files['image']
        #print(f">>>>><<<<{request.args['prompt']}")
        prompt=request.args['prompt']
        #print("HHHHHEHEHHE")
        # Read the image via file.stream
        img = pl.open(file.stream)
        res=queryimage(getvision(),prompt,img)
    
        return jsonify({'msg': 'success', 'size': [img.width, img.height], 'desc':res})
    except:
        return jsonify({'msg': 'fail', 'desc':"Error Processing image."})
        


