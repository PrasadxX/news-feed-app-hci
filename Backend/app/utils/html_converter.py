from bs4 import BeautifulSoup
import re
import html2markdown

def clean_html(html_content: str) -> str:
    """
    Clean HTML content by removing unnecessary tags and attributes.
    """
    if not html_content:
        return ""
   
    # Parse HTML with BeautifulSoup
    soup = BeautifulSoup(html_content, 'html.parser')
   
    # Remove script and style elements
    for script in soup(["script", "style"]):
        script.decompose()
       
    # Remove iframe elements
    for iframe in soup.find_all('iframe'):
        iframe.decompose()
   
    # Clean up specific tags
    for tag in soup.find_all():
        # Remove style attributes
        if tag.has_attr('style'):
            del tag['style']
           
        # Remove class attributes
        if tag.has_attr('class'):
            del tag['class']
   
    # Return cleaned HTML
    return str(soup)
def clean_markdown_content(markdown_content: str) -> str:
    """
    Clean markdown content by removing unwanted patterns and formatting.
    """
    if not markdown_content:
        return ""
    
    # Remove all variations of COLOMBO (News1st) patterns from anywhere in the text
    patterns = [
        r'COLOMBO \(News 1st\)[-\s]*',
        r'COLOMBO \(News1st\)[-\s]*', 
        r'COLOMBO\(News1st\)[-\s]*',
        r'__COLOMBO\(News1st\)[-\s]*',
        r'COLOMBO \(News1st\)[-\s]*',
        r'COLOMBO \(News 1st\)[-\s]*'
    ]
    
    for pattern in patterns:
        markdown_content = re.sub(pattern, '', markdown_content, flags=re.IGNORECASE)
    
    # Remove any remaining leading/trailing whitespace
    markdown_content = markdown_content.strip()
    
    # Remove excessive underscores
    markdown_content = re.sub(r'_{2,}', '', markdown_content)
    
    # Remove excessive newlines but preserve proper spacing
    markdown_content = re.sub(r'\n{3,}', '\n\n', markdown_content)
    
    # Clean up markdown headers
    markdown_content = re.sub(r'^#+\s*', '', markdown_content, flags=re.MULTILINE)
    
    return markdown_content
def enhance_sinhala_markdown(markdown_content: str) -> str:
    """
    Enhance Sinhala markdown content with appropriate emojis but no bold formatting.
    """
    if not markdown_content:
        return ""
    
    # List of emojis for various topics
    emoji_dict = {
        'අමාත්‍ය|මන්ත්‍රී|ජනාධිපති|රජය': '🏛',
        'සෞඛ්‍ය|රෝහල්|වෛද්‍ය': '🏥',
        'ඩිජිටල්|තාක්ෂණ|පරිගණක': '💻',
        'සාකච්ඡා|රැස්වීම|සම්මන්ත්‍රණ': '🤝',
        'ව්‍යාපෘති|සැලැස්ම|වැඩසටහන්': '📊',
        'අධ්‍යාපන|පාසල්|විශ්වවිද්‍යාල': '🎓',
        'ආර්ථික|මුදල්|ව්‍යාපාර': '💰',
        'කෘෂිකාර්මික|ගොවි|වගා': '🌾',
        'නවීන|පුවත්|සංවෘත්තිය|නිරුපණ': '📰',
        'සංස්කෘතිය|චිත්‍රපට|සංගීත': '🎬',
        'සමාජ|අදහස්|බලපෑම': '🌍',
        'විද්‍යා|අනාවැකි|ගවේෂණ': '🔬',
        'නීති|අයිතිය|අධිකරණ': '⚖️',
        'විදේශ|අන්තර්ජාතික': '🌏',
        'ඉංජිනේරු|අවශ්‍යතා|සැලැස්ම': '🛠',
        'කලා|සංස්කෘතික|නිරූපණ': '🎨'
    }
    
    # Split the content into paragraphs
    paragraphs = markdown_content.split('\n\n')
    
    # Process each paragraph
    formatted_paragraphs = []
    
    for i, para in enumerate(paragraphs):
        # Skip empty paragraphs
        if not para.strip():
            continue
            
        # First paragraph gets news emoji but no bold formatting
        if i == 0:
            formatted_para = f"{para} 📰"
        else:
            # Check and append emoji based on the content
            added_emoji = ''
            for keyword, emoji in emoji_dict.items():
                if re.search(keyword, para):
                    added_emoji = emoji
                    break
            
            formatted_para = f"{para} {added_emoji}" if added_emoji else para
        
        formatted_paragraphs.append(formatted_para)
    
    # Join the paragraphs with double newlines for Markwon compatibility
    return "\n\n".join(formatted_paragraphs)


def html_to_markdown(html_content: str) -> str:
    """
    Convert HTML content to clean Markdown format.
    """
    if not html_content:
        return ""
    
    # First clean the HTML
    cleaned_html = clean_html(html_content)
    
    # Convert to Markdown
    markdown_content = html2markdown.convert(cleaned_html)
    
    # Clean the resulting markdown (this is where the COLOMBO pattern gets removed)
    markdown_content = clean_markdown_content(markdown_content)
    
    # Enhance Sinhala content if non-ASCII characters are present
    if any(ord(c) > 128 for c in markdown_content):
        markdown_content = enhance_sinhala_markdown(markdown_content)
    
    # Final cleanup pass just to be sure
    markdown_content = clean_markdown_content(markdown_content)
    
    # Replace newlines with <br> tags
    markdown_content = markdown_content.replace('\n', '<br>')
    
    return markdown_content
