/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.classpath;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationEnumValue;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.fineract.ServerApplication;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Dummy {
    public static void main(String... args) throws Exception {
        List<Class> CLASSES = Arrays.asList(OneToOne.class, OneToMany.class, ManyToMany.class, ManyToOne.class);

        List<String> result = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(ServerApplication.class.getPackageName()).scan()) {
            final AtomicInteger counter = new AtomicInteger();

            for(Class clazz : CLASSES) {
                ClassInfoList annotations = scanResult.getClassesWithFieldAnnotation(clazz);

                ClassInfoList fetchClassInfos = annotations.filter(classInfo -> classInfo.getFieldInfo().stream().anyMatch(fieldInfo -> {
                    AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(clazz);

                    if (annotationInfo == null) {
                        return false;
                    }

                    boolean match = "EAGER".equals(AnnotationEnumValue.class.cast(annotationInfo.getParameterValues().getValue("fetch")).getValueName());

                    return match;
                }));

                result.addAll(fetchClassInfos.stream().map(info -> "fineract-provider/src/main/java/" + info.getName().replaceAll("\\.", "/") + ".java").toList());
            }

            System.out.println(">>>>>>>>>>>>>>>>>> size: " + result.size());

            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder
                .readEnvironment()
                .findGitDir()
                .build();

            // https://stackoverflow.com/questions/4777453/looping-over-commits-for-a-file-with-jgit

            /*
            BlameCommand blameCommand = new BlameCommand(repository);

            for(String path : result) {
                blameCommand.setFilePath(path);
                BlameResult blameResult = blameCommand.call();

                RawText rawText = blameResult.getResultContents();
                Set<Integer> iterations = new HashSet<>();
                Set<Integer> gitSourceLines = new HashSet<>();


                for (int i = 0; i < rawText.size(); i++) {
                    iterations.add(i);
                    gitSourceLines.add(blameResult.getSourceLine(i));
                    System.out.println("i = " + i + ", getSourceLine(i) = " + blameResult.getSourceLine(i) + blameResult.getSourceCommit(i).);
                }
                System.out.println("iterations size: " + iterations.size());
                System.out.println("sourceLines size: " + gitSourceLines.size());
            }
            */


            /*
            try (Git git = new Git(repository)) {
                // Status status = git.status().call();

                Ref head = repository.getRefDatabase().findRef("HEAD");

                // a RevWalk allows to walk over commits based on some filtering that is defined
                RevWalk walk = new RevWalk(repository);
                walk.sort(RevSort.COMMIT_TIME_DESC);

                RevCommit commit = walk.parseCommit(head.getObjectId());
                RevTree tree = commit.getTree();
                System.out.println("Having tree: " + tree);

                TreeWalk treeWalk = new TreeWalk(repository);
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                treeWalk.setFilter(OrTreeFilter.create(result.stream().map(PathFilter::create).collect(Collectors.toList())));

                ObjectReader reader = git.getRepository().newObjectReader();

                // old
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                ObjectId oldTree = git.getRepository().resolve( "HEAD^{tree}" ); // equals newCommit.getTree()
                oldTreeIter.reset( reader, oldTree );

                // new
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                ObjectId newTree = git.getRepository().resolve( "HEAD~1^{tree}" ); // equals oldCommit.getTree()
                newTreeIter.reset( reader, newTree );

                for(String path : result) {
                    List<DiffEntry> diff = git.diff()
                        .setOldTree(oldTreeIter)
                        .setNewTree(newTreeIter)
                        // .setPathFilter(PathFilter.create(path))
                        .call();

                    for (DiffEntry entry : diff) {
                        System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
                        try (DiffFormatter formatter = new DiffFormatter(System.out)) {
                            formatter.setRepository(repository);
                            formatter.format(entry);
                        }
                    }
                }

                while (treeWalk.next()) {
                    Iterable<RevCommit> log = git.log()
                        .addPath(treeWalk.getPathString())
                        .add(repository.resolve("remotes/origin/develop"))
                        .call();
                    for(RevCommit last : log) {
                        if(last.getAuthorIdent().getWhenAsInstant().isAfter(Instant.parse("2021-10-24T00:00:00Z"))) {
                            System.out.println("!!! : " + treeWalk.getPathString() + " - " + last.getAuthorIdent().getWhenAsInstant());
                            // System.out.println("    - " + getContent(git, last, treeWalk.getPathString()));
                            getContent(git, last, treeWalk.getPathString());
                        }
                    }
                }
            }
            */

            try (Git git = new Git(repository)) {
                experiment1(git);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void experiment1(Git git) {
        try (OutputStream stream = new ByteArrayOutputStream()) {
            List<DiffEntry> list = git.diff().setOutputStream(stream).call();
            BufferedReader reader = new BufferedReader(new StringReader(stream.toString()));
            for (DiffEntry entry : list) {
                String line = reader.readLine();
                StringWriter diff = new StringWriter();
                while (line != null && !line.startsWith("diff")) {
                    diff.append(line);
                    diff.write('\n');
                    line = reader.readLine();
                }
                System.out.println(">>> " + line);
            }
            getContent(null, null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getContent(Git git, RevCommit commit, String path) throws Exception {
        if(git==null) {
            return null;
        }

        try (TreeWalk treeWalk = TreeWalk.forPath(git.getRepository(), path, commit.getTree())) {
            ObjectId blobId = treeWalk.getObjectId(0);
            try (ObjectReader objectReader = git.getRepository().newObjectReader()) {
                ObjectLoader objectLoader = objectReader.open(blobId);
                byte[] bytes = objectLoader.getBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        }
    }
}
