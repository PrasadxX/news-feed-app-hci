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

def html_to_markdown(html_content: str) -> str:
    """
    Convert HTML content to Markdown format.
    """
    if not html_content:
        return ""
    
    # First clean the HTML
    cleaned_html = clean_html(html_content)
    
    # Convert to Markdown
    markdown_content = html2markdown.convert(cleaned_html)
    
    # Clean up the markdown
    markdown_content = re.sub(r'\n{3,}', '\n\n', markdown_content)  # Remove excessive newlines
    
    return markdown_content.strip()