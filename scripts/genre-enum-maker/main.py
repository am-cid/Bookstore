import requests
from bs4 import BeautifulSoup


def main():
    URL = "https://selfpublishing.com/list-of-book-genres/"
    resp = requests.get(URL)
    if resp.status_code == 200:
        soup = BeautifulSoup(resp.text, "html.parser")
        raw_genres = map(
            lambda x: x.a.text.split(" ", 1)[1].replace("\u00a0", ""),
            soup.find_all("li", attrs={"class": "ez-toc-heading-level-3"}),
        )
        raw_genres = sorted(raw_genres)
        with open("genres.txt", "w") as f:
            for genre in raw_genres:
                enum_name = to_enum(genre)
                f.write(f'{enum_name}("{genre}"),\n')
    else:
        print("Request not successful. status code ", resp.status_code)


def to_enum(string: str) -> str:
    new_string = ""
    for char in string.upper():
        if char in [" ", "/", "-", "\t", "\n", "\0", "\b", "\r"]:
            new_string += "_" if new_string[len(new_string) - 1] != "_" else ""
        elif char.isalnum():
            new_string += char
        else:
            ...
    return new_string


if __name__ == "__main__":
    main()
