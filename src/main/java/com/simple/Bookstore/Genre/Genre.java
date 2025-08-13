package com.simple.Bookstore.Genre;

import com.simple.Bookstore.Exceptions.UnreachableException;
import lombok.Getter;

/**
 * Source: "<a href="https://selfpublishing.com/list-of-book-genres/">The Master List of Book Genres: 95 Fiction & Nonfiction Genres</a>"</br>
 * used this script to extract and create enum names mapped to UI friendly equivalent:
 * <pre><block>
 * import requests
 * from bs4 import BeautifulSoup
 *
 * def main():
 *     URL = "https://selfpublishing.com/list-of-book-genres/"
 *     resp = requests.get(URL)
 *     if resp.status_code == 200:
 *         soup = BeautifulSoup(resp.text, "html.parser")
 *         raw_genres = map(
 *             lambda x: x.a.text.split(" ", 1)[1].replace("\u00a0", ""),
 *             soup.find_all("li", attrs={"class": "ez-toc-heading-level-3"}),
 *         )
 *         raw_genres = sorted(raw_genres)
 *         with open("genres.txt", "w") as f:
 *             for genre in raw_genres:
 *                 enum_name = to_enum(genre)
 *                 f.write(f'{enum_name}("{genre}"),\n')
 *     else:
 *         print("Request not successful. status code ", resp.status_code)
 *
 * def to_enum(string: str) -> str:
 *     new_string = ""
 *     for char in string.upper():
 *         if char in [" ", "/", "-", "\t", "\n", "\0", "\b", "\r"]:
 *             new_string += "_" if new_string[len(new_string) - 1] != "_" else ""
 *         elif char.isalnum():
 *             new_string += char
 *         else:
 *             ...
 *     return new_string
 *
 * if __name__ == "__main__":
 *   main()
 * </code></pre>
 */
@Getter
public enum Genre {
    ACTION_THRILLER("Action thriller"),
    ACTION_ADVENTURE_FICTION("Action/Adventure fiction"),
    APOCALYPTIC_SCI_FI("Apocalyptic sci-fi"),
    ART_PHOTOGRAPHY("Art & photography"),
    AUTOBIOGRAPHY_MEMOIR("Autobiography/Memoir"),
    BIOGRAPHY("Biography"),
    BODY_HORROR("Body horror"),
    CAPER("Caper"),
    CHILDRENS_FICTION("Children’s fiction"),
    CLASSIC_FICTION("Classic fiction"),
    COLONIZATION_SCI_FI("Colonization sci-fi"),
    COMEDY_HORROR("Comedy horror"),
    CONSPIRACY_THRILLER("Conspiracy thriller"),
    CONTEMPORARY_FICTION("Contemporary fiction"),
    CONTEMPORARY_ROMANCE("Contemporary romance"),
    COZY_MYSTERY("Cozy mystery"),
    DARK_FANTASY("Dark fantasy"),
    DARK_ROMANCE("Dark romance"),
    DISASTER_THRILLER("Disaster thriller"),
    EROTIC_ROMANCE("Erotic romance"),
    ESPIONAGE_THRILLER("Espionage thriller"),
    ESSAYS("Essays"),
    FAIRY_TALES("Fairy tales"),
    FANTASY("Fantasy"),
    FANTASY_ROMANCE_ROMANTASY("Fantasy romance (Romantasy)"),
    FOLKTALES("Folktales"),
    FOOD_DRINK("Food & drink"),
    FORENSIC_THRILLER("Forensic thriller"),
    GOTHIC_HORROR("Gothic horror"),
    GOTHIC_ROMANCE("Gothic romance"),
    GRAPHIC_NOVEL("Graphic novel"),
    GUMSHOE_DETECTIVE_MYSTERY("Gumshoe/Detective mystery"),
    HARD_SCI_FI("Hard sci-fi"),
    HEROIC_FANTASY("Heroic fantasy"),
    HIGH_FANTASY("High fantasy"),
    HISTORICAL_FANTASY("Historical fantasy"),
    HISTORICAL_FICTION("Historical fiction"),
    HISTORICAL_MYSTERY("Historical mystery"),
    HISTORICAL_ROMANCE("Historical romance"),
    HISTORICAL_THRILLER("Historical thriller"),
    HISTORY("History"),
    HORROR("Horror"),
    HOW_TO_GUIDES("How-To/Guides"),
    HOWDUNNITS("Howdunnits"),
    HUMANITIES_SOCIAL_SCIENCES("Humanities & social sciences"),
    HUMOR("Humor"),
    LGBTQ("LGBTQ+"),
    LEGAL_THRILLER("Legal thriller"),
    LITERARY_FICTION("Literary fiction"),
    LOCKED_ROOM_MYSTERY("Locked room mystery"),
    LOVECRAFTIAN_COSMIC_HORROR("Lovecraftian/Cosmic horror"),
    LOW_FANTASY("Low fantasy"),
    MAGICAL_REALISM("Magical realism"),
    MILITARY_SCI_FI("Military sci-fi"),
    MIND_UPLOADING_SCI_FI("Mind uploading sci-fi"),
    MYSTERY("Mystery"),
    MYTHIC_FANTASY("Mythic fantasy"),
    NEW_ADULT("New adult"),
    NOIR("Noir"),
    PARALLEL_WORLD_SCI_FI("Parallel world sci-fi"),
    PARANORMAL_HORROR("Paranormal horror"),
    PARANORMAL_ROMANCE("Paranormal romance"),
    PARANORMAL_THRILLER("Paranormal thriller"),
    PARENTING("Parenting"),
    PHILOSOPHY("Philosophy"),
    POST_APOCALYPTIC_HORROR("Post-apocalyptic horror"),
    PROCEDURAL_HARD_BOILED_MYSTERY("Procedural/Hard-boiled mystery"),
    PSYCHOLOGICAL_HORROR("Psychological horror"),
    PSYCHOLOGICAL_THRILLER("Psychological thriller"),
    QUIET_HORROR("Quiet horror"),
    REGENCY("Regency"),
    RELIGION_SPIRITUALITY("Religion & spirituality"),
    RELIGIOUS_THRILLER("Religious thriller"),
    ROMANCE("Romance"),
    ROMANTIC_COMEDY("Romantic comedy"),
    ROMANTIC_SUSPENSE("Romantic suspense"),
    SATIRE("Satire"),
    SCI_FI_ROMANCE("Sci-fi romance"),
    SCIENCE_TECHNOLOGY("Science & technology"),
    SCIENCE_FICTION("Science fiction"),
    SELF_HELP("Self-help"),
    SHORT_STORY("Short story"),
    SLASHER("Slasher"),
    SOFT_SCI_FI("Soft sci-fi"),
    SPACE_OPERA("Space opera"),
    SPACE_WESTERN("Space western"),
    STEAMPUNK("Steampunk"),
    SUPERNATURAL_MYSTERY("Supernatural mystery"),
    THRILLER("Thriller"),
    TRAVEL("Travel"),
    TRUE_CRIME("True crime"),
    URBAN_FANTASY("Urban fantasy"),
    WESTERN("Western"),
    WOMENS_FICTION("Women’s fiction"),
    YOUNG_ADULT("Young adult");


    // for computing styling class
    private final static int length = values().length;
    private static final int numStylingClasses = 3;
    private static final int[] groupEnds;

    // calculate distribution of remainders:
    // e.g. 95 genres split into 3 groups will have a remainder of 2. This 2
    // will be split evenly across the first two groups. The distribution will
    // always be 1.
    static {
        // e.g. 95 / 3 = 31 genres for each class
        int remainder = length % numStylingClasses;
        // e.g. 95 % 3 = 2 genres to be distributed
        int baseGroupSize = length / numStylingClasses;
        groupEnds = new int[numStylingClasses];

        int currentEnd = 0;
        for (int i = 0; i < numStylingClasses; i++) {
            currentEnd += baseGroupSize + (i < remainder ? 1 : 0);
            groupEnds[i] = currentEnd;
        }
    }

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

    public String getStyleClass() throws UnreachableException {
        int ordinal = ordinal();
        for (int i = 0; i < numStylingClasses; i++) {
            if (ordinal < groupEnds[i]) {
                return String.format("genre-color-style-%02d", i);
            }
        }
        throw new UnreachableException("Genre.getStyleClass()");
    }
}
