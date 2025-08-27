import os
import shutil
import subprocess
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path
from urllib.parse import urlparse

import yaml

BASE16_SCHEMES_SOURCE_GIT_URL = "https://github.com/chriskempson/base16-schemes-source"
BASE16_DEFAULT_SCHEMES_GIT_URL = (
    "https://github.com/chriskempson/base16-default-schemes"
)
ROOT_DIR = Path.cwd()


def main():
    repo = ROOT_DIR / clone(BASE16_SCHEMES_SOURCE_GIT_URL)
    os.chdir(repo)
    with open("list.yaml", "r") as f:
        themes: dict[str, str] = yaml.safe_load(f)
    Path.mkdir(ROOT_DIR / "themes", exist_ok=True)
    os.chdir(ROOT_DIR / "themes")

    with ThreadPoolExecutor() as executor:
        futures = [
            executor.submit(clone_and_clean, theme_name, github_url)
            for theme_name, github_url in themes.items()
        ]
        futures.append(
            executor.submit(
                clone_and_clean,
                urlparse(BASE16_DEFAULT_SCHEMES_GIT_URL).path.strip("/").split("/")[-1],
                BASE16_DEFAULT_SCHEMES_GIT_URL,
            )
        )
        for future in as_completed(futures):
            try:
                future.result()
            except Exception as e:
                print(f"error in task: {e}")


def clone_and_clean(theme_name: str, github_url: str):
    "clones git repo and removes all non .yml and LICENSE files"
    print(f"cloning {theme_name} from {github_url}...")
    repo = ROOT_DIR / "themes" / clone(github_url)
    os.chdir(repo)
    for path in repo.rglob("*"):
        if path.is_file():
            if "LICENSE" in path.name:
                print("    found LICENSE")
            elif path.suffix in {".yml", ".yaml"}:
                with open(path, "r") as f:
                    scheme = yaml.safe_load(f)
                if scheme.get("scheme"):
                    print(f"    found scheme: {path.name}")
                else:
                    path.unlink()
            else:
                print(f"    removing file: {path}")
                path.unlink()
        else:
            shutil.rmtree(path)
    print(f"finished cleaning {repo}")
    os.chdir(ROOT_DIR / "themes")


def clone(url: str) -> str:
    "git clones a repo. returns the repo name"
    run(["git", "clone", url])
    repo_name = urlparse(url).path.strip("/").split("/")[-1]
    return repo_name


def run(command: list[str]) -> subprocess.CompletedProcess[str]:
    result = subprocess.run(
        command, stdout=subprocess.PIPE, stdin=subprocess.PIPE, text=True
    )
    if result.returncode != 0:
        print(f"Error running command: {' '.join(command)}")
        print(result.stderr)
    return result


if __name__ == "__main__":
    main()
